# Manorrock Aegean

This project delivers a HTTP-based Git repository server.

## Using the Docker image to deploy server.

```
  docker run --rm -d -p 8080:8080 -v $PWD:/root/.manorrock/aegean/repos manorrock/aegean:VERSION
```

And replace VERSION with the version you want to use.

_Note_ in the command line above we have mapped the `/root/.manorrock/aegean/repos`
to point to the current directory so we can persist the Git repositories outside
of the container.

To verify the contain is up and running execute the command below:

```
git clone http://localhost:8080/repos/test.git
```

You should see output similar to below:

```
Cloning into 'test'...
warning: You appear to have cloned an empty repository.
```

Congratulations you are now running Manorrock Aegean!

## Testing SNAPSHOT versions

Every night we push a SNAPSHOT version to Docker Hub. If you want to give the
version under development a test drive use `snapshot` as the version for the
instructions above.

## Important notice

Note if you file issues or answer questions on the issue tracker and/or issue 
pull requests you agree that those contributions will be owned by Manorrock.com
and that Manorrock.com can use those contributions in any manner Manorrock.com
so desires.