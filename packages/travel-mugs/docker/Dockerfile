# Build from packages/travel-mugs:  docker build -f docker/Dockerfile -t iamnazzal/gopromotional-mugday:1.0.0 .
FROM node:20-alpine

LABEL org.opencontainers.image.title="gopromotional-mugday" \
      org.opencontainers.image.description="Caffeine tracker with half-life estimate, sleep-ready time and reusable-cup stats." \
      org.opencontainers.image.url="https://www.gopromotional.co.uk/branded-products/travel-mugs" \
      org.opencontainers.image.source="https://github.com/nbkdevs/GoPromotional" \
      org.opencontainers.image.documentation="https://github.com/nbkdevs/GoPromotional/tree/main/packages/travel-mugs/docker" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.vendor="GoPromotional" \
      org.opencontainers.image.authors="GoPromotional"

WORKDIR /app
COPY npm/package.json npm/README.md npm/LICENSE ./
COPY npm/lib ./lib
COPY npm/bin ./bin
RUN chmod +x bin/cli.js && ln -s /app/bin/cli.js /usr/local/bin/gopromotional-mugday \
    && mkdir -p /data && chown node:node /data

ENV GOPROMOTIONAL_MUGDAY_FILE=/data/mugday.json
WORKDIR /data
VOLUME ["/data"]
USER node
ENTRYPOINT ["gopromotional-mugday"]
CMD ["--help"]
