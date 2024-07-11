# Spring Boot and HTMX: The boring app

Start with the simplest app possible and evolve from there. 

In this repo I implement a sample full stack app with Spring Boot and HTMX.
Apache Freemarker is used as a template language and Bootstrap as a CSS framework.

For complex interaction heavy use cases the sample includes a mechanism to render a vue app at a specific route as well.

The accompanying blog post can be read [in this repo](docs/article_part_1.md)
and [on my employers blog](https://www.codecentric.de/wissens-hub/blog/spring-boot-and-htmx-the-boring-app)

## Start the spring app with embedded vue app

Run `./gradlew bootRun` and open http://localhost:8080

Note: the build artifacts of the vue app are checked in at the correct places for your convenience.

## Start vue app in standalone mode

```shell
cd vue-app
yarn install
yarn dev
```

open http://localhost:5173/

## Build a new version of the vue app to be included in the Spring Boot app

```shell
yarn install
yarn build
```

Note: The binary `rsync` has to be present in the PATH

## Deploy to AWS

This will build your lambda as jar and deploy it inside a standard java lambda runtime. For better boot times this will
also utilize the AWS SnapStart feature. Make sure to have the [AWS sam cli](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html#install-sam-cli-instructions) installed. Log into your target AWS account and then run:

```shell
sam build
sam deploy --guided
```

The app can be reached at the URL shown at the bottom of this command.
Be aware that the first request after ~15 Minutes of inactivity takes around 6 seconds to initialize the app again.
Subsequent requests should be fast.