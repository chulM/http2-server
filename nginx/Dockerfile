FROM nginx:1.15.3

RUN mkdir -p /prod/logs/nginx
COPY nginx /etc/nginx
WORKDIR /etc/nginx
EXPOSE 80 443

VOLUME ["/etc/nginx"]
CMD ["nginx", "-g", "daemon off;"]