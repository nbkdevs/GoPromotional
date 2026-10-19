# Build from packages/tote-bags:  docker build -f docker/Dockerfile -t iamnazzal/gopromotional-packlist:1.0.1 .
FROM node:20-alpine

LABEL org.opencontainers.image.title="gopromotional-packlist" \
      org.opencontainers.image.description="Packing checklists with templates, per-night quantities and a Markdown format." \
      org.opencontainers.image.url="https://www.gopromotional.co.uk/branded-products/tote-bags" \
      org.opencontainers.image.source="https://github.com/nbkdevs/GoPromotional" \
      org.opencontainers.image.documentation="https://github.com/nbkdevs/GoPromotional/tree/main/packages/tote-bags/docker" \
      org.opencontainers.image.version="1.0.1" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.vendor="GoPromotional" \
      org.opencontainers.image.authors="GoPromotional"

WORKDIR /app
COPY npm/package.json npm/README.md npm/LICENSE ./
COPY npm/lib ./lib
COPY npm/bin ./bin
RUN chmod +x bin/cli.js && ln -s /app/bin/cli.js /usr/local/bin/gopromotional-packlist \
    && mkdir -p /data && chown node:node /data

WORKDIR /data
VOLUME ["/data"]
USER node
ENTRYPOINT ["gopromotional-packlist"]
CMD ["--help"]
