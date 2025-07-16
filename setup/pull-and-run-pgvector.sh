#!/bin/bash

podman kill -a && podman rm -a
podman run -p 5432:5432 -e POSTGRES_PASSWORD=postgres  pgvector:pg17