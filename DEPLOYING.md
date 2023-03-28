<a name="readme-top"></a>
# Vireo Deployment Guide


## Configuration

There are several files that allow for configuration:

* The `.env` file.
* The `src/main/resources/application.yml` file.
* The `build/appConfig.js.template` file.

Most settings can and should be configured using the `.env` file when using `docker-compose`.
In other cases, either passing environment variables to `docker`, manually exporting environment variables, or directly editing the configuration files may be necessary.

The `src/main/resources/application.yml` file can be configured through environment variables.
Consult the [Spring Documentation][spring-docs-binding] in regards to this.

The `build/appConfig.js.template` has limited support for environment variables but for those that are exposed may be altered using the `.env` file.

## Production Deployments

For **production** deployments, deploy using `docker-compose`.
This is the recommended method of deployment for production systems.

Perform the following steps to deploy:

```shell
git clone https://github.com/TAMULib/Vireo.git Vireo

cd Vireo/

cp example.env .env

# Make any changes to the .env file before here (see the configuration sections).
docker-compose up
```

The **development** deployment can also use `docker-compose` in the same way.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Deployment using only Docker

To manually use `docker` rather than `docker-compose`, run the following:

```shell
docker image build -t vireo .
docker run -it vireo
```

## Development Deployment using NPM and Maven

Manual deployment can be summed up by running:

**Maven**

```shell
mvn clean spring-boot:run
```

Those steps are a great way to start but they also fail to explain the customization that is often needed.
There are multiple ways to further configure this for deployment to better meet the desired requirements.

It is highly recommended only to perform *manual installation* when developing.
For production deployment, please use either the `docker-compose` method or the **Docker** method above.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


### Directly Configuring the `dist/appConfig.js` File

This method of configuration works by altering the built distribution configuration file.
This file is deleted every time either `npm run build` or `npm run clean` are run.
But in the event that a quick and manual change is needed, this is the simplest way to do so.


<div align="right">(<a href="#readme-top">back to top</a>)</div>


### Directly Configuring the `.wvr/build-config.js` Build File

This method of configuration is only recommended for `advanced uses` but is otherwise not recommended.
The advantage of this method of configuration is that of preserving the changes between _build_ or _clean_ commands.
There is only a small section that should be changed.

The `.wvr/build-config.js` file has only a single section of interest and might look something like this:

```js
    {
      from: './build/appConfig.js.template',
      to: './appConfig.js',
      transform(content) {
        return content
          .toString()
          .replace('${AUTH_SERVICE_URL}', 'http://localhost:9000/mock/auth')
          .replace('${STOMP_DEBUG}', 'false');
      },
    },
```

In the above example snippet, only the lines containing `'${AUTH_SERVICE_URL}'` and `'${STOMP_DEBUG}'` should be changed.
For example `'https://labs.library.tamu.edu/auth/2x'` could be changed to `'https://labs.library.tamu.edu:8443/auth/2x'` (changing the port number from 443 to 8443).

Once this is done all of the steps from *Development Deployment using NPM and Maven* above can be followed.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


<!-- LINKS -->
[weaver-ui]: https://github.com/TAMULib/Weaver-UI-Core

[spring-docs-binding]: https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-external-config.html#boot-features-external-config-relaxed-binding
