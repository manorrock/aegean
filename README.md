# Manorrock Aegean

[![build](https://github.com/manorrock/aegean/actions/workflows/build.yml/badge.svg)](https://github.com/manorrock/aegean/actions/workflows/build.yml)

This project delivers a HTTP-based Git repository server.

## Start the server

In an empty directory of your choice use the following command line to start 
Manorrock Aegean:

```shell
  docker run --rm -d -it -p 8080:8080 -v $PWD:/mnt ghcr.io/manorrock/aegean
```

## Verify the server is up and running

To verify the container is up and running execute the command below:

```
git clone http://localhost:8080/aegean/repositories/test.git
```

You should see output similar to below:

```
Cloning into 'test'...
warning: You appear to have cloned an empty repository.
```

Congratulations you are now running Manorrock Aegean!

## How do I prevent anonymous users from creating new repositories?

To prevent anonymous users from creating new repositories you can set the AEGEAN_ADMIN_USERNAME and AEGEAN_ADMIN_PASSWORD environment variables. See the example below:

```shell
  docker run --rm -d -it -p 8080:8080 -v $PWD:/mnt -e AEGEAN_ADMIN_USERNAME=admin -e AEGEAN_ADMIN_PASSWORD=adminadmin ghcr.io/manorrock/aegean
```

Note that if you want to create a new repository you will need to use the -c extraHeader option with the git clone command to provide the admin credentials
as illustrated below:

```shell
  git clone -c http.extraHeader="Authorization: Basic $(echo -n 'admin:adminadmin' | base64)" http://localhost:8080/aegean/repositories/test.git
```

Once you have done this the repository is created and available for any user to clone.

## Can I disable anonymous access to the server?

Yes, you can disable anonymous access to the server by setting the AEGEAN_ANONYMOUS_DISABLED environment variable to true. Note that if you do so you will also need to provide the admin credentials as described above to be able to interact with the server.

```shell
  docker run --rm -d -it -p 8080:8080 -v $PWD:/mnt -e AEGEAN_ANONYMOUS_DISABLED=true ghcr.io/manorrock/aegean
```

## What is the future of this project?

This project will not be developed into a full blown code hosting platform, 
but rather keep it simple and focus on delivering simple Git repository 
hosting for personal use. If you need more than that we recommend you look at
GitHub, GitLab, Bitbucket, Gitea, GitBucket or any of the other code
hosting platforms available.

## Technologies used

The following technologies where used to deliver this project:

1. [Eclipse JGit](https://www.eclipse.org/jgit/)
2. [Jakarta CDI](https://jakarta.ee/specifications/cdi/)
3. [Jakarta Servlet](https://jakarta.ee/specifications/servlet/)
4. [Jakarta Security](https://jakarta.ee/specifications/security/)
5. [JUnit 5](https://junit.org/junit5/)
6. [Playwright](https://playwright.dev/)

## How do I contribute?

See [Contributing](CONTRIBUTING.md)

## Our code of Conduct

See [Code of Conduct](CODE_OF_CONDUCT.md)


## Important notice

Note if you file issues or answer questions on the issue tracker and/or issue 
pull requests you agree that those contributions will be owned by Manorrock.com
and that Manorrock.com can use those contributions in any manner Manorrock.com
so desires.
