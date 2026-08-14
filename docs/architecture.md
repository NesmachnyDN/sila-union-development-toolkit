# Architecture

## Goals

The toolkit is designed around four constraints:

1. automation logic must be testable without a live SILA Union instance;
2. portfolio code must not depend on internal corporate infrastructure or data;
3. repository integration must be replaceable without changing application use cases;
4. examples must remain small enough to understand during a technical interview.

## Component model

```text
+-----------------------------------------------------------+
| Application / use-case layer                              |
|                                                           |
|  GlossaryImportService       ArchitectureReportService    |
+-----------------------------+-----------------------------+
                              |
                              v
                    +----------------------+
                    | SilaRepositoryClient |
                    |      (port)          |
                    +----------+-----------+
                               |
                +--------------+--------------+
                |                             |
                v                             v
+-------------------------------+   +-------------------------+
| InMemorySilaRepositoryStub    |   | Real vendor adapter     |
|                               |   |                         |
| synthetic elements/relations  |   | intentionally absent    |
| deterministic test behavior   |   | from public portfolio   |
+-------------------------------+   +-------------------------+
```

## Architecture decisions

### AD-01 — Keep the core vendor-neutral

The domain records use only the fields needed by the portfolio scenarios. They do not reproduce a proprietary vendor metamodel. Mapping to a real repository would belong in an adapter.

### AD-02 — All tests use an in-memory stub

The test suite must not depend on network connectivity, credentials, repository IDs, a VPN, or a vendor installation. This makes CI deterministic and prevents accidental access to real environments.

### AD-03 — Deterministic IDs

Portfolio examples derive stable UUIDs from synthetic external keys. This makes repeat runs predictable and allows idempotent behavior to be tested.

### AD-04 — No real adapter in the public baseline

A real SILA Union adapter is out of scope until it can be implemented exclusively from publicly redistributable API contracts and dependencies. This boundary is intentional, not a missing test fixture.
