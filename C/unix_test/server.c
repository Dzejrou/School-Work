#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <netdb.h>
#include <err.h>
#include <errno.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <fcntl.h>

#define EVER ;;
#define BUFF_SIZE 1024

uint16_t next_port(uint16_t, uint16_t, uint16_t);
void process_user(uint8_t*, ssize_t, uint16_t, struct addrinfo*, socklen_t, int,
                  struct addrinfo*);
int init(const char*, struct addrinfo*);

int
main(int argc, char** argv)
{
    if(argc != 4)
    {
        printf("Usage: [%s] <port> <lbound> <ubound>\n", argv[0]);
        return EXIT_FAILURE;
    }

    uint16_t lbound, ubound, curr;
    lbound = atoi(argv[2]);
    ubound = atoi(argv[3]);
    curr = lbound;

    struct addrinfo client, hint;
    bzero(&hint, sizeof(hint));
    hint.ai_family = AF_UNSPEC;
    hint.ai_socktype = SOCK_DGRAM;
    hint.ai_flags = AI_PASSIVE;
    int sock = init(argv[1], &hint);
    curr = next_port(curr, lbound, ubound);

    // Incoming connections.
    socklen_t clen = sizeof(client);
    bzero(&client, sizeof(client));

    uint8_t buff[BUFF_SIZE];
    ssize_t sz;
    for( EVER )
    {
        if((sz = recvfrom(sock, buff, BUFF_SIZE, 0,
           (struct sockaddr*)&client, &clen)) == -1)
            err(1, "recvfrom");

        switch(fork())
        {
            case -1:
                errx(1, "fork fail");
                break;
            case 0:
                process_user(buff, sz,curr, &client, clen, sock, &hint);
                printf("Child [%d] ending.\n", getpid());
                return EXIT_SUCCESS;
            default:
                curr = next_port(curr, lbound, ubound);
                break;
        }
    
    }

    return EXIT_SUCCESS;
}

uint16_t
next_port(uint16_t curr, uint16_t lb, uint16_t ub)
{ // Returns next port in the range.
    return (++curr % (ub - lb)) + lb;
}

void
process_user(uint8_t* buff, ssize_t sz, uint16_t port,
             struct addrinfo* client, socklen_t clen, int sock,
             struct addrinfo* hint)
{
    char* fname = (char*)malloc(sizeof(char) * sz);
    fname = strncpy(fname, (char*)buff, sz);
    int fd = open("received", O_WRONLY | O_CREAT | O_TRUNC, 0666);

    printf("Got file name: %s\n", fname);

    char port_s[6];
    snprintf(port_s, 6, "%u", port);
    
    printf("Sending new port: %s\n", port_s);
    if((sz = sendto(sock, (char*)port_s, 6, 0,
       (struct sockaddr*)client, clen)) != 6)
        err(1, "sendto: %ld", sz);

    close(sock); // No longer needed.
    sock = init(port_s, hint);


    while((sz = recvfrom(sock, buff, BUFF_SIZE, 0, (struct sockaddr*)client, &clen)) > 0)
    {
        printf("Received: %ld\n", sz);
        write(fd, buff, sz);
    }

    free(fname);
    close(sock);
    close(fd);
}

int
init(const char* port, struct addrinfo* hint)
{
    struct addrinfo *res, *res_orig;
    int e;
    if((e = getaddrinfo(NULL, port, hint, &res_orig) != 0))
            errx(1, "%s", gai_strerror(e));

    int sock;
    int found = 0;

    int hasipv6 = 0;
    for(res = res_orig; res != NULL; res = res->ai_next)
    { // Check if IPv6 is possible.
        if(res->ai_family == AF_INET6)
            hasipv6 = 1;
    }
    for(res = res_orig; res != NULL; res = res->ai_next)
    {
        if(hasipv6 == 1 && res->ai_family != AF_INET6)
            continue;

        if(res->ai_socktype != SOCK_DGRAM || res->ai_protocol != IPPROTO_UDP)
            continue;
        sock = socket(res->ai_family, res->ai_socktype, res->ai_protocol);

        if(!bind(sock, res->ai_addr, res->ai_addrlen))
        {
            found = 1;
            break;
        }
        else
            close(sock);
    }

    if(res == NULL && found == 0)
        err(1, "bad addr");
    freeaddrinfo(res_orig);

    return sock;
}
