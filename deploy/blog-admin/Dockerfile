FROM nginx:alpine

RUN rm /usr/share/nginx/html/*

COPY dist/ /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 5137

CMD ["nginx", "-g", "daemon off;"]