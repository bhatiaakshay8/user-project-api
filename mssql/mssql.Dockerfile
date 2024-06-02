# Dockerfile for a containerized SQL Server
# File is used by docker-compose.docker-db.yml
FROM mcr.microsoft.com/mssql/server:2022-latest
COPY --chmod=755 ./initdb.sh /usr/bin/initdb.sh
SHELL ["/bin/bash","-c"]
ENTRYPOINT ["/usr/bin/initdb.sh"]
