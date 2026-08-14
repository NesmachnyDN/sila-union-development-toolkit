# Publication checklist

Before pushing new material to this public repository:

- [ ] `./scripts/check-public-safety.sh` passes.
- [ ] `mvn clean verify` passes where Maven is available.
- [ ] New examples use synthetic names, IDs, URLs, and data only.
- [ ] No files were copied from an internal repository or corporate document set.
- [ ] No real credentials, endpoints, private IPs, object IDs, screenshots, or templates are present.
- [ ] Any new vendor integration code relies only on API contracts and dependencies that are permitted for public use.
- [ ] README/docs accurately distinguish the in-memory stub from a real SILA Union integration.
- [ ] AI-assisted implementation is described accurately when it is material to the project.
