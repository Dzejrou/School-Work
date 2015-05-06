## Init:
set.seed(13031992) # Seeded by the author's date of birth.
echo <- FALSE # Do not output code into console.
delim <- "[------------]"
cat(delim,"\n")
print("NMAI059 Homework by Jaroslav Jindrak.")
cat(delim,"\n\n")

##
# Function that simulates a single day in the warehouse.
# Param: start -> The store starts to accept orders at this hour.
# Param: end   -> The store stops to accept orsers at this hour.
# Returns: List of product order numbers.
##
simulate_day <- function(start = 8,end = 22)
{
  day <<- list() # For returning the number of orders in this day.
  curr_time = 0 # 0 represents start hour
  lambda = 1 / 4 # 15 per hour -> 4 minute intervals on average.
  max_time = (end - start) * 60
  day$orders1 = 0
  day$orders2 = 0
  
  while(curr_time <= max_time)
  { # Iterate over the opening hours.
    curr_time = curr_time + rexp(1, lambda) # Increment time according to the exp distribution.
    if(curr_time > max_time)
      break # Passed the opening hours.
    
    day$orders1 = day$orders1 + 1 # First item is bought always.
    if(sample(1:2,1) == 1) # 50% for the second item to be ordered ALONG with the first one.
      day$orders2 = day$orders2 + 1
  }
  return(day)
}

##
# Function that simulates the entire year in the warehouse.
# Param: Number of days in the year that the shop is open.
# Returns: List of two vectors - one for each product.
##
simulate_year <- function(opening_days = 360)
{
  year <<- list()
  year$orders1 <- rep(0,opening_days) # Preallocation of the vector! Otherwise looping would be too slow.
  year$orders2 <- rep(0,opening_days)
  
  for(i in 1:opening_days)
  {
    day <- simulate_day(8,22)
    year$orders1[i] <- day$orders1
    year$orders2[i] <- day$orders2
  }
  return(year)
}

##
# Function that returns the interval estimate of a given vector of values.
# Param: Vector of values to estimate.
# Returns: 2 element vector, first element represents the lower bound,
#          second represents the upper bound.
##
interval <- function(values = c())
{
  std_deviation <- sd(values) # Standart deviation of values.
  std_error <- std_deviation / sqrt(length(values)) # Standart error of the mean.
  error <- qnorm(.95) * std_error # Margin of error.
  
  # Create the interval:
  int <- c(mean(values) - error, mean(values) + error)
  return(int)
}

##
# Function for debug purposes that formates time.
# Param: Current time in minutes.
# Param: Time offset in hours.
# Returns: A string in the format ?d:?h:?m standing for current time.
##
get_time <- function(t, start = 8)
{
  mins <- t %% 60
  mins <- mins %/% 1 # Get rid of seconds.
  hours <- t %/% 60 + start
  days <- hours %/% 24 # In case we got to another day.
  hours <- hours - days * 24
  return(sprintf("%dd:%dh:%dm",days,hours,mins))
}

##
# Function that simulates one day of orders and deliveries.
# Param: Number of couriers employed and available on the current day.
# Param: Mean for delivery time (exp distribution).
# Param: Time that the couriers have between deliveries for a smoke/snack.
# Param: Time the store opens.
# Param: Time the store closes for orders (deliveries are still made after this point).
# Returns: A report structure containing the $deliv_time vector of delivery times and
#          the $wait_time vector of wait times for all the orders recieved this day.
# Note: Change this debug variable to TRUE if you want messages on each order/delivery:
  debug <- FALSE
##
simulate_couriers <- function(num_of_couriers = 7, delivery_time = 30, wait_time = 0,
                              start = 8, end = 22)
{
  # Courier representation:
  couriers <- list()
  couriers$deliv = rep(0,num_of_couriers) # At what times does he finnish his delivery? 0 if not delivering.
  couriers$wait = rep(wait_time,num_of_couriers) # Wait time in minutes, -Inf means he is on a delivery mission.
  # Assigning wait_time, so the couriers can start delivering right off the start of the day.
  
  # Create the queue:
  day <- list()
  day$start <- 1 # Start of the queue.
  day$end <- 1 # End of the queue.
  day$orders <- list()
  # Preallocation of 250 values (because of speed) -1 (to know if it was used or not).
  day$orders$queue <- rep(-1,250) # Order times.
  day$orders$exp_time <- rep(-1,250) # Expedition time for each order.
  day$orders$deliv_time <- rep(-1,250) # Delivery time for each order.
  day$curr_time = 0
  day$max_time = (end - start) * 60 # Max time for accepting orders.
  day$lambda_order = 1 / 4 # Parameter for the exp distribution - 4 minutes interval from task #1.
  day$lambda_deliv = 1 / delivery_time # Parameter for the exp distribution with mean = 30 min.
  
  while(!((day$start == day$end && day$end != 1) && day$curr_time >= day$max_time)) # There are orders to deliver.
  {
    delta_time = rexp(1,day$lambda_order)
    day$curr_time = day$curr_time + delta_time # Next order.
    
    if(day$curr_time <= day$max_time) # Can we accept new orders?
    {
      # Add new order.
      day$orders$queue[day$end] = day$curr_time
      
      # Debugging information:
      if(debug)
        print(sprintf("Got order #%d at time %s.",day$end,get_time(day$curr_time)))
      
      day$end <- day$end + 1
    }
    
    for(i in 1:num_of_couriers)
    {
      # Increment the waiting times for the courier (if possible):
      if(couriers$wait[i] >= 0)
        couriers$wait[i] <- couriers$wait[i] + delta_time
      
      # If the courier finnished his delivery mark him as finished.
      if(couriers$wait[i] == -Inf & couriers$deliv[i] <= day$curr_time)
      {
        couriers$wait[i] <- 0
        couriers$deliv[i] <- 0
      }

      # Assign deliveries to available couriers.
      if(day$start != day$end & couriers$wait[i] >= wait_time)
      {
       
        couriers$wait[i] <- -Inf
        deliv_time <- rexp(1,day$lambda_deliv)
        couriers$deliv[i] <- day$curr_time + deliv_time # Generate a delivery time.
        
        
        # Mark order as post-expedition.
        day$orders$exp_time[day$start] <- day$curr_time
        day$orders$deliv_time[day$start] <- day$curr_time + deliv_time
        day$start <- day$start + 1 # Move the queue.
        
        # Debugging information: 
        if(debug)
          print(sprintf("Assigning delivery #%d to courier #%d at time %s to be delivered at %s.",day$start-1,
                        i,get_time(day$curr_time),get_time(day$orders$deliv_time[day$start - 1])))
      }
    }  
  }
  
  report <- list()
  tmp_deliv <- day$orders$deliv_time - day$orders$exp_time # Delivery times for all orders.
  tmp_wait <- day$orders$exp_time - day$orders$queue # Wait times for all orders.
  
  report$deliv_times <- tmp_deliv[1:day$end-1] # Resize
  report$wait_times <- tmp_wait[1:day$end-1]
  report$last <- day$orders$deliv_time[day$start-1] # For task 3a

  return(report)
}

##
# Brief: Function used to calculate the average delivery time of the last order - task 3a.
# Returns: String containing the average delivery time for the last order.
##
last_deliv <- function()
{
  last_deliv <- rep(0,100) # Preallocate.
  index <- 1
  for(i in 1:100)
  {
    # Simulate the entire year with couriers, keeping the delivery times of the last order.
    result <- simulate_couriers()
    last_deliv[index] <- result$last
    index <- index + 1
  }
  result <- get_time(mean(last_deliv))
  return(result)
}

# Output for task no. 1:
day <- simulate_day(8,22)
print("Task #1:")
print(sprintf("Orders of product #1: %d", day$orders1))
print(sprintf("Orders of product #2: %d", day$orders2))
cat(delim,"\n\n")

# Output for task no. 2:
year <- simulate_year(360)
print("Task #2:")
print("a - Point estimation: ")
print(sprintf(" | Orders of product #1: %f", mean(year$orders1)))
print(sprintf(" | Orders of product #2: %f", mean(year$orders2)))
print("a - Interval estimation: ")
int1 <- interval(year$orders1) # Calculate interval estimate for the first product.
int2 <- interval(year$orders2) # Calculate interval estimate for the second product.
print(sprintf(" | Orders of product #1: (%f, %f)",int1[1],int1[2]))
print(sprintf(" | Orders of product #2: (%f, %f)",int2[1],int2[2]))
test1 <- t.test(year$orders1, mu = 196) # Test the hypothesis.
test2 <- t.test(year$orders2, mu = 98)

print("b: Test results below:")
print(sprintf(" | Test #1: p-value = %e, sig. level = 0.05.",test1$p.value))
if(test1$p.value < 0.05) {# If p-value is lower than the significance level the null hypothesis is rejected.
  print(" -| Null hypothesis rejected, the mean differs from 196!")
}else{
  print(" -| Null hypothesis proved, the mean does not differ from 196!")
}

print(sprintf(" | Test #2: p-value = %e, sig. level = 0.05.",test2$p.value))
if(test2$p.value < 0.05) {
  print(" -| Null hypothesis rejected, the mean differrs from 98!")
}else{
  print(" -| Null hypothesis proved, the mean does differ from 98!")
}

print("c: See plots --->")
# Draw the first histogram:
h1 <- hist(year$orders1, col = "thistle", xlab = "orders of product #1", ylab = "# of days", main = "Histogram for product #1:")
# Add the normal distribution lines:
x1 <- seq(min(year$orders1), max(year$orders1), length = 100)
y1 <- dnorm(x1, mean = mean(year$orders1), sd = sd(year$orders1))
y1 <- y1 * diff(h1$breaks)[1] * length(year$orders1)
lines(x1, y1, lwd = 2)

# Draw the second histogram:
h2 <- hist(year$orders2, col = "magenta", xlab = "orders product #2", ylab = "# of days", main = "Histogram for product #2:")
# Add the normal distribution lines:
x2 <- seq(min(year$orders2), max(year$orders2), length = 100)
y2 <- dnorm(x2, mean = mean(year$orders2), sd = sd(year$orders2))
y2 <- y2 * diff(h2$breaks)[1] * length(year$orders2)
lines(x2, y2, lwd = 2)

# Shapiro-Wilk normality test:
# First product:
shapiro_wilk <- shapiro.test(year$orders1)
print("d: Shapiro - Wilk test:")
print(sprintf(" | Order #1: p-value = %f, assuming sig. level of 0.05.",shapiro_wilk$p.value))
if(shapiro_wilk$p.value < 0.05) {
  print(" -| p-value lower than 0.05, the sample deviates from normality.")
}else{
  print(" -| p-value not lower than 0.05, the sample passed the normality test.")
}

# Second product:
shapiro_wilk <- shapiro.test(year$orders2)
print(sprintf(" | Order #2: p-value = %f, assuming sig. level of 0.05.",shapiro_wilk$p.value))
if(shapiro_wilk$p.value < 0.05) {
  print(" -| p-value lower than 0.05, the sample deviates from normality.")
}else{
  print(" -| p-value not lower than 0.05, the sample passed the normality test.")
}
cat(delim,"\n\n")

# Output for task no. 3:
print("Task #3:")
report <- simulate_couriers(7,30,0,8,22) # Number of couriesrs and wait time given.

# NOTE: Due to bad understanding of the task 3a, I have calculated both the average delivery time and the average
#       delivery time for the last order over the course of 100 days (this makes a permormance spike though, that
#       can be felt as few seconds long halt of this application).
avg_deliv <- mean(report$deliv_time)
avg_wait <- mean(report$wait_time)
print(sprintf("a: Average delivery time: %f",avg_deliv))
print(sprintf("a: Average delivery time of the last order: %s",last_deliv()))
print("b:")
print(sprintf(" | Average wait time: %f",avg_wait))
total_wait <- sum(report$wait_time)
print(sprintf(" | Total wait time: %f",total_wait))
print(" | For histogram see plots --->")
hist(report$wait_time, col = "cyan", xlab = "wait time (in minutes)", ylab = "# of orders", main = "Histogram for wait times:")
prob <- pnorm(5,mean(report$wait_time),sd(report$wait_time))
print(sprintf("c: Probability that the wait time will be lower than 5 mins: %f %%.",prob * 100))
# My choices for the courier count and break time for the couriers between delivery missions:
num_cour <- 15
break_time <- 10
print(sprintf("d: # of couriers - %d, break time - %d minutes",num_cour,break_time))
# debug <- TRUE # Uncomment to see courier simulation reports.
report <- simulate_couriers(num_cour,30,break_time,8,22)
total_wait <- sum(report$wait_time)
avg_wait <- mean(report$wait_time)
prob <- pnorm(5,mean(report$wait_time),sd(report$wait_time))
print("e: Results for new courier count and break time:")
print(sprintf(" | Total wait time: %f",total_wait))
print(sprintf(" | Average wait time: %f",avg_wait))
print(sprintf(" | Probability that the wait time will be lower than 5 mins: %f %%.",prob * 100))
cat(delim,"\n")