# Manorrock Aegean

This project delivers a HTTP-based Git repository server.

## Using the Docker image to deploy server.

```
  docker run --rm -d -p 8080:8080 -v $PWD:/root/.manorrock/aegean/repos manorrock/aegean:VERSION
```

And replace VERSION with the version you want to use, or if you want to try out
the version currently under development you can use `snapshot` as the version.

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
