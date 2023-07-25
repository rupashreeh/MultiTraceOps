#!/bin/bash
set -B                  # enable brace expansion
for i in {1..1000}; do
  curl -X POST http://localhost:8090/addBalance -H 'Content-Type: application/json' -d '{"custid":301,"balance":100}'
  curl http://localhost:8090/balance/301
done
