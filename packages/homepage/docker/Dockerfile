# Build from packages/homepage:  docker build -f docker/Dockerfile -t iamnazzal/gopromotional-event-planner:1.0.1 .
FROM node:20-alpine

LABEL org.opencontainers.image.title="gopromotional-event-planner" \
      org.opencontainers.image.description="Event countdown, planning timeline, giveaway quantities and print order-by dates." \
      org.opencontainers.image.url="https://www.gopromotional.co.uk/" \
      org.opencontainers.image.source="https://github.com/nbkdevs/GoPromotional" \
      org.opencontainers.image.documentation="https://github.com/nbkdevs/GoPromotional/tree/main/packages/homepage/docker" \
      org.opencontainers.image.version="1.0.1" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.vendor="GoPromotional" \
      org.opencontainers.image.authors="GoPromotional"

WORKDIR /app
COPY npm/package.json npm/README.md npm/LICENSE ./
COPY npm/lib ./lib
COPY npm/bin ./bin
RUN chmod +x bin/cli.js && ln -s /app/bin/cli.js /usr/local/bin/gopromotional-event-planner \
    && mkdir -p /data && chown node:node /data

WORKDIR /data
VOLUME ["/data"]
USER node
ENTRYPOINT ["gopromotional-event-planner"]
CMD ["--help"]
