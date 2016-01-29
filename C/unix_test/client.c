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
#include <arpa/inet.h>
#include <netinet/in.h>
#include <fcntl.h>
#include <poll.h>
#include <signal.h>

#define EVER ;;
#define BUFF_SIZE 1024

int init(const char*, const char*, struct addrinfo*);
uint16_t get_port(uint8_t, uint8_t);

int
main(int argc, char** argv)
{
    if(argc != 4)
    {
        printf("Usage: [%s] <file> <server> <port>\n", argv[0]);
        return EXIT_FAILURE;
    }

    struct addrinfo hint;
    bzero(&hint, sizeof(hint));
    hint.ai_family = AF_UNSPEC;
    hint.ai_socktype = SOCK_DGRAM;

    int sock = init(argv[2], argv[3], &hint);

    uint8_t buff[BUFF_SIZE];
    size_t fname_sz = strlen(argv[1]);
    strncpy((char*)buff, argv[1], fname_sz);

    ssize_t sz;
    if((sz = send(sock, buff, fname_sz, 0)) != fname_sz)
        err(1, "send fname");

    if((sz = recv(sock, buff, 6, 0)) != 6)
        err(1, "recv port");

    char port[6];
    strncpy(port, (char*)buff, 6);
    printf("Received port: %s\n", port);

    close(sock);
    sock = init(argv[2], port, &hint);

    poll(NULL, 0, 50); // TODO: Server has to open the new port.
    int fd = open(argv[1], O_RDONLY | O_CREAT, 0666);
    ssize_t fsz;
    while((fsz = read(fd, buff, BUFF_SIZE)) > 0)
    {
        printf("From file: %ld\n", fsz);
        if((sz = send(sock, buff, fsz, 0)) < 0)
            err(1, "send");
        printf("Sent: %ld\n", sz);
    }
    // Notify about end.
    if((sz = send(sock, buff, 0, MSG_EOR)) < 0)
        err(1, "send");
    close(fd);
    close(sock);
}

int
init(const char* addr, const char* port, struct addrinfo* hint)
{
    struct addrinfo *res, *res_orig;
    int e;
    if((e = getaddrinfo(addr, port, hint, &res_orig) != 0))
            errx(1, "%s", gai_strerror(e));

    int sock;
    for(res = res_orig; res != NULL; res = res->ai_next)
    {
        switch(res->ai_family)
        {
            case AF_INET6:
                printf("INET6\n");
                break;
            case AF_INET:
                printf("INET\n");
                break;
            default:
                printf("OTHER: %d", res->ai_family);
                continue;
        }
        if(res->ai_socktype != SOCK_DGRAM || res->ai_protocol != IPPROTO_UDP)
            continue;
        sock = socket(res->ai_family, res->ai_socktype, res->ai_protocol);

        if(!connect(sock, res->ai_addr, res->ai_addrlen))
            break;
        else
            close(sock);
    }

    if(res == NULL)
        errx(1, "bad addr");
    freeaddrinfo(res_orig);

    return sock;

}
