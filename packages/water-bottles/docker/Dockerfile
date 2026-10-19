# Build from packages/water-bottles:  docker build -f docker/Dockerfile -t iamnazzal/gopromotional-siplog:1.0.0 .
FROM node:20-alpine

LABEL org.opencontainers.image.title="gopromotional-siplog" \
      org.opencontainers.image.description="Daily water goal estimate and a simple hydration log with streaks." \
      org.opencontainers.image.url="https://www.gopromotional.co.uk/branded-products/branded-water-bottles" \
      org.opencontainers.image.source="https://github.com/nbkdevs/GoPromotional" \
      org.opencontainers.image.documentation="https://github.com/nbkdevs/GoPromotional/tree/main/packages/water-bottles/docker" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.vendor="GoPromotional" \
      org.opencontainers.image.authors="GoPromotional"

WORKDIR /app
COPY npm/package.json npm/README.md npm/LICENSE ./
COPY npm/lib ./lib
COPY npm/bin ./bin
RUN chmod +x bin/cli.js && ln -s /app/bin/cli.js /usr/local/bin/gopromotional-siplog \
    && mkdir -p /data && chown node:node /data

ENV GOPROMOTIONAL_SIPLOG_FILE=/data/siplog.json
WORKDIR /data
VOLUME ["/data"]
USER node
ENTRYPOINT ["gopromotional-siplog"]
CMD ["--help"]
