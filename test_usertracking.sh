#!/usr/bin/env bash

export DEFAULT_HOSTNAME=127.0.0.1
export DEFAULT_PORT=8080

echo --------
curl -i -X POST http://${DEFAULT_HOSTNAME}:${DEFAULT_PORT}/service/contact -H "Content-Type: application/json" -d '
{
    "email": "teste@resultadosdigitais.com",
    "url": "www.resultadosdigitais.com.br",
    "datetime": "20172710 101010"
}
'
echo
