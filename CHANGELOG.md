## v1.2.8 (2026-01-12)

### Fix

- **publish**: pom issue
- **publish**: find replace error
- **publish**: mvn repo namespace needed to change also
- **publish**: not a token issue after all - seems to be a requirement that namespace starts com.github

## v1.2.7 (2026-01-12)

### Fix

- **publish**: trying a PAT instead

## v1.2.6 (2026-01-12)

### Fix

- **publish**: Github actor/token configuration
- **publish**: github actor/token not being set properly

## v1.2.5 (2026-01-12)

### Fix

- **build**: namespace change for mvn repo artefacts
- **release**: seems groupId must reflect the namespace to publish to GitHub packages

## v1.2.4 (2026-01-12)

### Fix

- **publish**: cursor leading me astray again

## v1.2.3 (2026-01-12)

### Fix

- **publish**: invalid tags command on workflow

## v1.2.2 (2026-01-12)

### Fix

- **publish**: reverting to manual mvn deploy

## v1.2.1 (2026-01-12)

### Fix

- **deps**: update dependency software.amazon.awssdk:bom to v2.41.5 (#1)
- **release**: reverting due to lack of clarity on GitHub branch protection rules

## v1.2.0 (2026-01-12)

### Feat

- **publish**: attempting to publish the public health dependency

### Fix

- **deploy**: wanting to auto-merge a version bump without relaxing rulesets too much
- **publish**: going with a marketplace action as far cleaner
- **publish**: configures Maven for GitHub Packages when credentials are provided
- **publish**: secrets not recognised by an actions include

## v1.1.1 (2026-01-11)

### Fix

- **pages**: jekyll config to ignore cursor

## v1.1.0 (2026-01-11)

### Feat

- **health**: adding health checks and a whole load of boilerplate to get CI up and running

### Fix

- **cz**: git history not deep enough yet to support the hygience check as configured
