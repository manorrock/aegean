# Manorrock Aegean

[![build](https://github.com/manorrock/aegean/actions/workflows/build.yml/badge.svg)](https://github.com/manorrock/aegean/actions/workflows/build.yml)

This project delivers a HTTP-based Git repository server.

## Deploy the WAR file

Deploy the WAR file on your Servlet container / application server as per the
instructions of your vendor.

## Running using the container image from DockerHub

In an empty directory of your choice use the following command line to start 
Manorrock Aegean.

```shell
  docker run --rm -d -it -p 8080:8080 -v $PWD:/mnt manorrock/aegean
```

## Verify the server is up and running

To verify the container is up and running execute the command below:

```
git clone http://localhost:8080/repositories/test.git
```

You should see output similar to below:

```
Cloning into 'test'...
warning: You appear to have cloned an empty repository.
```

Congratulations you are now running Manorrock Aegean!

## How do I contribute?

See [Contributing](CONTRIBUTING.md)

## Our code of Conduct

See [Code of Conduct](CODE_OF_CONDUCT.md)

## Important notice

Note if you file issues or answer questions on the issue tracker and/or issue 
pull requests you agree that those contributions will be owned by Manorrock.com
and that Manorrock.com can use those contributions in any manner Manorrock.com
so desires.
