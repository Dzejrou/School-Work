// DU3sch.hpp
// Jaroslav Jindrak NPRG051 2014/2015
#pragma once

#include <cstddef>
#include <random>
#include <limits>
#include <deque>
#include <map>
#include <memory>
#include <thread>
#include <atomic>
#include <future>

/**
 * Note: This solution uses N asyncs and futures and N threads for non-void tasks.
 *       (2 * N thread and no asyncs for void tasks)
 *       Void task specializations are at the bottom of the source file.
 */

/**
 * Brief: Forward declaration of the scheduler class.
 */
template<typename T, typename TASK>
class Scheduler;

/**
 * Brief: Simple struct used to hold the result of a task and the
 *        information if the task has been completed already.
 */
template<typename T>
struct task_register
{
    bool done{false};
    T result;
};

/**
 * Brief: Simple struct used to hold the task and it's id, so that the
 *        core executing the task knows where to store the result.
 */
template<typename TASK>
struct task_pack
{
    TASK task;
    std::size_t id;
};

/**
 * Brief: Class representing a processor core in this program, has it's
 *        queue of tasks and is called by the Scheduler to execute them.
 */
template<typename T, typename TASK>
class core
{
    public:
        /**
         * Brief: Default constructor of the core class.
         */
        core()
            : running{false}
        { /* DUMMY BODY */ }

        /**
         * Brief: Adds a task to the core's queue in a thread safe manner.
         * Param: Task pack to be added, containing the task and it's id.
         */
        void add_task(task_pack<TASK>&& task)
        {
            que_mutex_.lock();
            queue_.push_back(task);
            que_mutex_.unlock();
        }

        /**
         * Brief: Returns the size of the queue of this core, used for
         *        choosing a core for a task and for choosing steal target
         *        in the Scheduler class.
         */
        std::size_t queue_size()
        {
            return queue_.size();
        }

        /**
         * Brief: Executes the task thats sits first in the queue and sets
         *        the result in the appropriate task register.
         * Param: Pointer to the scheduler so the core can easily set the
         *        task result in the task register container.
         */
        void execute(Scheduler<T, TASK>* sched)
        {
            task_pack<TASK> tmp_pack;
            try
            {
                tmp_pack = pop_front();
            }
            catch(...)
            {
                // The queue got emptied after the call to this method.
                return; // No need to continue here.
            }

            current_task_id = tmp_pack.id;

            // Execute the task itself and store it's result in the Scheduler.
            auto fut = std::async(std::move(tmp_pack.task));
            sched->set_result(current_task_id, fut.get(), true);
        }

        /**
         * Brief: Returns the first element in the queue and removes
         *        it from said queue in a thread safe manner.
         */
        task_pack<TASK> pop_front()
        {
            // This method will be used for stealing and thus has to be
            // thread safe.
            que_mutex_.lock();
            if(queue_.empty())
            {
                // The queue got emptied before this method locked the mutex,
                // throw to signal the problem to the caller.
                que_mutex_.unlock(); // But don't create a deadlock...
                throw 0;
            }
            auto tmp = queue_.front();
            queue_.pop_front();

            que_mutex_.unlock();
            return tmp;
        }

        std::atomic<bool> running; // Will be changed from the Scheduler so has
                                   // to be atomic.
    private:
        std::deque<task_pack<TASK>> queue_; // The task queue.

        std::mutex que_mutex_; // Mutex used to lock the queue.
        std::size_t current_task_id; // ID of the current task in execution.
};

/**
 * Brief: The main template version of the Scheduler class.
 */
template<typename T, typename TASK>
class Scheduler
{
    public:
        /**
         * Brief: Main constructor for this class.
         * Param: core_count Number of cores that can be used.
         */
        Scheduler(std::size_t core_count)
            : core_count_{core_count},
              cores_{new core<T, TASK>[core_count_]},
              core_threads_{new std::unique_ptr<std::thread>[core_count_]},
              rd_device_{}, rd_engine_{rd_device_()},
              rd_dist_{0,core_count - 1}
        { /* DUMMY BODY */ }

        /**
         * Brief: Copy constructor, created because of std::random_device
         *        and std::mutex because they have explicitly deleted
         *        copy constructor, making this class also non-copyable.
         * Param: The other instance of this class. (const ref)
         */
        Scheduler(const Scheduler<T, TASK>& o)
            : core_count_{o.core_count_},
              task_registers_{o.task_registers_},
              rd_device_{},
              rd_engine_{o.rd_engine_},
              rd_dist_{o.rd_dist_},
              cores_{new core<T, TASK>[core_count_]},
              core_threads_{new std::unique_ptr<std::thread>[core_count_]},
              register_mutex_{}
        { /* DUMMY BODY */ }

        /**
         * Brief: Destructor, deallocates heap objects.
         */
        ~Scheduler()
        {
            delete[] cores_;
            delete[] core_threads_;
        }

        /**
         * Brief: Adds task to a chosen core and if the core isn't running,
         *        resets it's thread to start the execution.
         * Param: The task to be executed.
         */
        std::size_t add_task(TASK task)
        {
            std::size_t core_id = get_core_id(); // Get a suitable core.
            std::size_t task_id = get_task_id(); // Find the smallest id.

            task_pack<TASK> tmp_pack;
            tmp_pack.id = task_id;
            tmp_pack.task = std::move(task);
            set_result(task_id, T(), false); // Create a record for this task.
            cores_[core_id].add_task(std::move(tmp_pack));

            if(!cores_[core_id].running)
            { // Re-start the core thread if it's not running.
                cores_[core_id].running = true;
                core_threads_[core_id].reset(
                        new std::thread(&Scheduler<T, TASK>::run_core, this, core_id)
                        );
                core_threads_[core_id]->detach();
            }
            
            return task_id;
        }

        /**
         * Brief: Checks if a given task has completed it's execution.
         * Param: ID of the task returned by the add_task method.
         */
        bool is_task_ready(std::size_t index)
        {
            return task_registers_[index].done;
        }

        /**
         * Brief: Returns the result of a given task and blocks (actively) if
         *        the task hasn't finished it's execution.
         * Param: ID of the task returned by the add_task method.
         * Note: Due to it's blocking nature, it is advised to call
         *       is_task_ready(std::size_t) before calling this method.
         */
        T get_task_result(std::size_t index)
        {
            while(!task_registers_[index].done); // Active block.

            T tmp = task_registers_[index].result;

            /**
             * Note: This mechanism will free indexes of tasks that have
             *       already returned their results for another use,
             *       theoretically increasing the number of overall tasks
             *       completed by this scheduler. It will, though, create
             *       small stall time when another thread want's to access
             *       the task register, because of this lock.
             *       So for a scheduler with small number of tasks expected,
             *       commenting out these three lines might improve the
             *       performance.
             */
            register_mutex_.lock();
            task_registers_.erase(task_registers_.find(index));
            register_mutex_.unlock();
            return std::move(tmp);
        }

        /**
         * Brief: Sets the result of a given task.
         * Param: ID of the task.
         * Param: Result of the task.
         * Param: Boolean indicating if the task has already completed.
         *        (Used as false for initial value.)
         */
        void set_result(std::size_t id, T res, bool done)
        {
            register_mutex_.lock();
            task_registers_[id].result = res;
            task_registers_[id].done = done;
            register_mutex_.unlock();
        }

    private:
        /**
         * Brief: Finds the core with the smallest queue
         *        and returns it's id (index in the core array).
         */
        std::size_t get_core_id()
        {
            std::size_t id{0};
            std::size_t min_queue_size{std::numeric_limits<std::size_t>::max()};

            for(std::size_t i = 0; i < core_count_; ++i)
            {
                if(cores_[i].queue_size() < min_queue_size)
                { // Store the new minimum.
                    id = i;
                    min_queue_size = cores_[i].queue_size();
                }
            }
            return id;
        }

        /**
         * Brief: Method to be run in the core thread, if the core
         *        is running, executes it's oldest task in the queue
         *        (or steals a task if neccessary) and if the core has
         *        no tasks and none can be stolen, stops the core.
         * Param: ID of the core to be run.
         */
        void run_core(std::size_t core_id)
        {
            task_pack<TASK> tmp_task;

            while(cores_[core_id].running)
            {
                if(cores_[core_id].queue_size() > 0)
                { // Still has unfinished tasks.
                    cores_[core_id].execute(this);
                }
                else if(can_steal())
                { // Steal from other cores (at random).
                    try
                    {
                        tmp_task = steal_task();
                    }
                    catch(...)
                    {
                        // The queue got emptied before steal_task() could
                        // finnish.
                        cores_[core_id].running = false;
                        return;
                    }

                    cores_[core_id].add_task(std::move(tmp_task));
                    cores_[core_id].execute(this);
                }
                else
                { // No task in the entire schedulet that could be run
                  // by this core.
                    cores_[core_id].running = false;
                    return;
                }
            }
        }

        /**
         * Brief: Checks if there is a core that has non-empty queue,
         *        returns true if there is, false otherwise.
         * NOTE: No need to check id against that of the caller, this function
         *       will be called only if the caller has empty queue.
         */
        bool can_steal()
        {
            for(std::size_t i = 0; i < core_count_; ++i)
            {
                if(cores_[i].queue_size() > 0)
                    return true; // Found a core with free tasks.
            }
            return false;
        }

        /**
         * Brief: Will steal a tank from a randomly chosen core that
         *        has non-empty queue of tasks.
         * NOTE: Will be called after can_steal(), otherwise
         *       this function would block until there is
         *       a core with non-empty queue and a different id.
         */
        task_pack<TASK> steal_task()
        {
            std::size_t id{get_rand()};
            
            // It is not needed to check if the id is different than
            // that of the caller, because the caller will call this
            // function only when his queue is empty.
            while(cores_[id].queue_size() == 0)
                id = get_rand(); // Find a random core from those that
                                 // have non-empty queue.
            return cores_[id].pop_front();
        }

        /**
         * Brief: Returns a random number in the range [0..core_count - 1],
         *        used to find a target for task stealing.
         */
        std::size_t get_rand()
        {
            return rd_dist_(rd_engine_);
        }

        /**
         * Brief: Finds the lowest key that is not used for a task. Although
         *        keeping and incremental counter for the ids is possible,
         *        if the scheduler would run longer the std::size_t would
         *        sometime reach it's maximum and in the worst case scenario
         *        the first task added (with id = 0) would still be running so
         *        simply using modulo is not good.
         */
        std::size_t get_task_id()
        {
            std::size_t tmp_id = 0;
            for(auto it = task_registers_.begin(); it != task_registers_.end();
                    ++it)
            {
                // The map is ordered by it's keys, so this will find the first
                // free key.
                if(it->first == tmp_id)
                    ++tmp_id; // Used, skip.
                else
                    break; // Found!
            }
            return tmp_id;
        }

        std::size_t core_count_; // Number of cores available.
        std::map<std::size_t, task_register<T>> task_registers_; // Results.
        std::mutex register_mutex_; // Mutex that locks the result container.

        /* Variables for the super simple random system from C++11! */
        std::random_device rd_device_;
        std::default_random_engine rd_engine_;
        std::uniform_int_distribution<std::size_t> rd_dist_;
        
        /* Will have fixed size (number of cores). */
        core<T, TASK>* cores_;
        std::unique_ptr<std::thread>* core_threads_;
};





/*****
    * vvvvv SPECIALIZATION AREA BELOW vvvvv *
*****/

/**
 * Brief: Void specialization of the core class.
 * Note: Ommiting method description comments due to the similarities
 *       between this version and the main templated one.
 */
template<typename TASK>
class core<void, TASK>
{
    public:
        core()
            : running{false}
        { /* DUMMY BODY */ }

        void add_task(task_pack<TASK>&& task)
        {
            que_mutex_.lock();
            queue_.push_back(task);
            que_mutex_.unlock();
        }

        std::size_t queue_size()
        {
            return queue_.size();
        }

        void execute(Scheduler<void, TASK>* sched)
        {
            task_pack<TASK> tmp_pack;
            try
            {
                tmp_pack = pop_front();
            }
            catch(...)
            {
                // The queue got emptied after the call to this method.
                return; // No need to continue here.
            }

            current_task_id = tmp_pack.id;

            std::thread th(std::move(tmp_pack.task));
            th.join(); // Just wait for the procedure to finnish.
            sched->set_result(current_task_id, 0, true);
        }

        task_pack<TASK> pop_front()
        {
            // This method will be used for stealing and thus has to be
            // thread safe.
            que_mutex_.lock();
            if(queue_.empty())
            {
                // The queue got emptied before this method locked the mutex,
                // throw to signal the problem to the caller.
                que_mutex_.unlock();
                throw 0;
            }
            auto tmp = queue_.front();
            queue_.pop_front();
            que_mutex_.unlock();
            return tmp;
        }

       std::atomic<bool> running;
    private:
        std::deque<task_pack<TASK>> queue_;

        std::mutex que_mutex_;
        std::size_t current_task_id;
};

/**
 * Brief: Void specialization of the scheduler class.
 * Note: Ommiting method description comments due to the similarities
 *       between this version and the main templated one.
 */
template<typename TASK>
class Scheduler<void, TASK>
{
    public:
        Scheduler(std::size_t core_count)
            : core_count_{core_count},
              rd_device_{}, rd_engine_{rd_device_()},
              rd_dist_{0, core_count_ - 1},
              cores_{new core<void, TASK>[core_count_]},
              core_threads_{new std::unique_ptr<std::thread>[core_count_]}
        { /* DUMMY BODY */ }

        Scheduler(const Scheduler<void, TASK>& o)
            : core_count_{o.core_count_},
              task_registers_{o.task_registers_},
              rd_device_{}, rd_engine_{o.rd_engine_},
              rd_dist_{o.rd_dist_},
              cores_{new core<void, TASK>[core_count_]},
              core_threads_{new std::unique_ptr<std::thread>[core_count_]}
        { /* DUMMY BODY */ }

        ~Scheduler()
        {
            delete[] cores_;
            delete[] core_threads_;
        }

        std::size_t add_task(TASK task)
        {
            std::size_t core_id = get_core_id(); // Get a suitable core.
            std::size_t task_id = get_task_id(); // Find the smallest id.

            task_pack<TASK> tmp_pack;
            tmp_pack.id = task_id;
            tmp_pack.task = std::move(task);
            set_result(task_id, 0, false); // Create a record for this task.
            cores_[core_id].add_task(std::move(tmp_pack));

            if(!cores_[core_id].running)
            { // If the core is available, execute the task immedietly.
              // The mutex will be unlocked at the end of the run_core method.
                cores_[core_id].running = true;
                core_threads_[core_id].reset(
                        new std::thread(&Scheduler<void, TASK>::run_core, this, core_id)
                        );
                core_threads_[core_id]->detach();
            }
            
            return task_id;
        }

        bool is_task_ready(std::size_t index)
        {
            return task_registers_[index];
        }

        void get_task_result(std::size_t index)
        {
            // Since this will be a procedure, just block till it's done.
            while(!task_registers_[index]);

            return;
        }

        void set_result(std::size_t id, int dummy, bool done)
        { // Dummy will be zero, ignore it.
            //std::cerr << "[" << id << "] Done." << std::endl;
            register_mutex_.lock();
            task_registers_[id] = done;
            register_mutex_.unlock();
        }
    private:
        std::size_t get_core_id()
        {
            std::size_t id{0};
            std::size_t min_queue_size{std::numeric_limits<std::size_t>::max()};

            for(std::size_t i = 0; i < core_count_; ++i)
            {
                if(cores_[i].queue_size() < min_queue_size)
                {
                    id = i;
                    min_queue_size = cores_[i].queue_size();
                }
            }
            return id;
        }

        void run_core(std::size_t core_id)
        {
            task_pack<TASK> tmp_task;

            while(cores_[core_id].running)
            {
                if(cores_[core_id].queue_size() > 0)
                { // Still has unfinished tasks.
                    cores_[core_id].execute(this);
                }
                else if(can_steal())
                { // Steal from other cores (at random).
                    try
                    {
                        tmp_task = steal_task();
                    }
                    catch(...)
                    {
                        // The queue got emptied before steal_task() could
                        // finnish.
                        cores_[core_id].running = false;
                        break;
                    }

                    cores_[core_id].add_task(std::move(tmp_task));
                    cores_[core_id].execute(this);
                }
                else
                { // No task in the entire schedulet that could be run
                  // by this core.
                    cores_[core_id].running = false;
                }
            }
        }

        bool can_steal()
        {
            for(std::size_t i = 0; i < core_count_; ++i)
            {
                if(cores_[i].queue_size() > 0)
                    return true; // Found a core with free tasks.
            }
            return false;
        }

        task_pack<TASK> steal_task()
        {
            std::size_t id{get_rand()};
            
            // It is not needed to check if the id is different than
            // that of the caller, because the caller will call this
            // function only when his queue is empty.
            while(cores_[id].queue_size() == 0)
                id = get_rand(); // Find a random core from those that
                                 // have non-empty queue.
            return cores_[id].pop_front();
        }

        std::size_t get_rand()
        {
            return rd_dist_(rd_engine_);
        }

        std::size_t get_task_id()
        {
            std::size_t tmp_id = 0;
            for(auto it = task_registers_.begin(); it != task_registers_.end();
                    ++it)
            {
                // The map is ordered by it's keys, so this will find the first
                // free key.
                if(it->first == tmp_id)
                    ++tmp_id; // Used, skip.
                else
                    break; // Found!
            }
            return tmp_id;
        }

        std::size_t core_count_;
        std::map<std::size_t, bool> task_registers_;
        std::mutex register_mutex_;

        /* Variables for the super simple random system from C++11! */
        std::random_device rd_device_;
        std::default_random_engine rd_engine_;
        std::uniform_int_distribution<std::size_t> rd_dist_;
        
        /* Will have fixed size (number of cores). */
        core<void, TASK>* cores_;
        std::unique_ptr<std::thread>* core_threads_;
};
