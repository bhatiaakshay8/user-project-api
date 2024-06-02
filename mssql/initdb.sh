#!/bin/bash

(sleep 10 && /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P Mssq1pas_ -Q "CREATE DATABASE [user-project-api-db]" -d master) &
exec /opt/mssql/bin/sqlservr --accept-eula
