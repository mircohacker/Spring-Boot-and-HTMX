Spring Boot and HTMX: Deployment to AWS Lambda
====

This is the next part of my series about Spring Boot and HTMX.

In this post, I will show you how to deploy the application created in the [previous post](https://www.codecentric.de/wissens-hub/blog/spring-boot-and-htmx-the-boring-app) to AWS Lambda. If you're in a hurry or impatient, you can simply check out the accompanying [Git Repo](https://github.com/mircohacker/Spring-Boot-and-HTMX) and follow the README to build and deploy the application to AWS Lambda.

A lot of coding tutorials stop at the last post when the app runs locally, but because I'm a DevOps guy, I will also take you on a short tour on how to actually deploy this application. The overall theme is again simplicity.

The target runtime for this app is AWS Lambda. The reason for this is simplicity. We don't need to handle host upgrades, high availability, scaling, and so forth. Also, for an app which is used only rarely or in bursts, it is unbeatable cheap. The long startup times of Spring Boot can be at least mitigated with the [SnapStart](https://docs.aws.amazon.com/lambda/latest/dg/snapstart.html) feature of AWS Lambda. When enabled, AWS takes a memory snapshot of the application after initialization and uses this snapshot for the next "cold start" of the lambda. This reduces the startup time significantly (~5 seconds vs. some 100 milliseconds).

This is, as always, not a silver bullet. The details on how long this snapshot is saved lies entirely within the responsibility of AWS. If AWS decides that your app is not "worth it", they can simply evict the snapshot and on the next cold start you have to wait 5 seconds again. But at least within one page-load, we do not have to wait for another cold start on subsequent requests like stylesheets or JS files. Another trick for faster cold start times is to avoid the Spring component scan and instead `@Import` the relevant classes directly.

As you probably know, AWS Lambdas cannot simply answer web requests. 
Instead, a lambda must actively poll pending executions from AWS. This is called the [Lambda runtime API (LRA)](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html). Fortunately, there is already a [Java implementation](https://github.com/aws/serverless-java-container) of this interface. It can receive requests from an API Gateway or a FunctionUrl and pass them to Spring Boot just like normal web requests. The newer implementation is with `SpringDelegatingLambdaContainerHandler`, much simpler to use but still contains various [sharp](https://github.com/aws/serverless-java-container/issues/858) [edges](https://github.com/aws/serverless-java-container/issues/860). This is why we use the older but also more mature `RequestStreamHandler` to implement the LRA. The exact implementation can be viewed [here](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/src/main/kotlin/org/example/ssrdemo/StreamLambdaHandler.kt). The relevant point is that we have to define this class as an entry point for our lambda.

The default account limit for concurrent lambda executions is 1000. To avoid an unexpected high cloud bill in case of going viral, we limit our lambda to 20 concurrent executions. We do this via the reserved concurrent executions property of our lambda. All requests exceeding this limit will return a `429 Too Many Requests` response. In a production setting, we should set an alarm for this case via the `Throttles` CloudWatch metric.

## Deployment method

In the pursuit of simplicity, we will deploy our application to lambda using the [AWS Serverless Application Model (SAM)](https://aws.amazon.com/serverless/sam/). This framework is a thin wrapper around [AWS CloudFormation](https://aws.amazon.com/de/cloudformation/), with some useful defaults already configured.

For a simple start, this is probably sufficient. But for more involved use cases, we can move to a framework which provides finer control like Terraform/OpenTofu, CDKTF, or AWS CDK. But like always in this post, we will run with the simplest possibility first and see how far it can carry us. The configuration of this SAM App is stored in the file [template.yml](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/template.yml).

Follow these steps to finally deploy our application into a live AWS account:

1. Log into the AWS account of your choice.

2. [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html#install-sam-cli-instructions).

3. Optional if you changed the Vue app: Run `yarn build` in the `vue-app` folder.

4. Execute `sam build` in the root folder again. This in turn will execute `gradle build` and create the necessary zip for the deployment to AWS. The magic of creating the zip is defined in the [`build.gradle`](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/build.gradle) file.

5. Execute `sam deploy --guided`. This will ask you for name and region of the app to be deployed and saves them into a file if you wish. If you saved the options, the next time you need to deploy, you can simply run `sam build && sam deploy`.

6. Click on the link printed by `SAM` and enjoy your application.

Now our application is functionally complete and deployed 🎉

## Next Steps

- Domain name: For a more polished UX, you should probably use a custom (sub) domain instead of the plain function URL.

- API Gateway: A real API gateway instead of the function URL provides useful mechanisms like throttling, caching, etc.

- Usage monitoring: You will need to monitor the actual usage of the application in order to decide if you should switch to another deployment model, e.g., a container on ECS. Spring supports [container image build via buildpacks](https://www.baeldung.com/spring-boot-docker-images#buildpacks). If you haven't heard of them, you should [have a look](https://buildpacks.io/).

- Native executable: You can also try to compile the application with [GraalVM](https://www.graalvm.org/) to an executable and deploy this as a Lambda or container. Disclaimer: In an earlier version of this post, I tried to do this, but it is highly non-trivial and also not simple 😉. For a starting point, you can have a look at the [official sample](https://github.com/aws/serverless-java-container/tree/main/samples/springboot3/pet-store-native). Extensive tests against the compiled application are necessary because the compiled application can behave and fail in surprising ways.

