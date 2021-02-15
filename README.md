# Manorrock Aegean

This project delivers a HTTP-based Git repository server.

## Deploy the server using Docker

```
  docker run --rm -d -p 8080:8080 -v $PWD:/root/.manorrock/aegean manorrock/aegean:VERSION
```

And replace VERSION with the version you want to use.

> _Note_ in the command line above we have mapped the 
> `/root/.manorrock/aegean` directory to point to the current directory so
> we can persist the Git repositories outside of the container.

Note you can find the available tags on [Docker Hub](https://hub.docker.com/r/manorrock/aegean)

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

## Testing our development version

Upon every build we push our development version to Docker Hub. If you want to
give the version under development a test drive use `latest` as the version for
the instructions above.

## Important notice

Note if you file issues or answer questions on the issue tracker and/or issue 
pull requests you agree that those contributions will be owned by Manorrock.com
and that Manorrock.com can use those contributions in any manner Manorrock.com
so desires.
