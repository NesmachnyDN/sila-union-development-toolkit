# Security and public-repository policy

This repository must remain usable without secrets or access to any private environment.

## Never commit

- usernames, passwords, API tokens, private keys, certificates, or session data;
- real SILA Union endpoint URLs or private IP addresses;
- internal repository identifiers or production/test object IDs;
- corporate documents, screenshots, templates, or configuration exports;
- local `.env`, `application-dev.properties`, or equivalent credential-bearing files.

Use synthetic examples only.

## Automated guardrail

`scripts/check-public-safety.sh` performs a deliberately conservative local/CI scan for selected internal-environment markers, token patterns, private IPv4 addresses, and forbidden credential-file names. It runs before Maven tests in GitHub Actions.

The script is defense in depth only. It cannot prove that a repository contains no confidential information, so manual review remains required before adding new fixtures or documentation.

## Reporting

If a secret is ever committed, treat it as compromised: revoke/rotate it first, then remove it from the repository and Git history. Deleting the current file alone is not sufficient.
