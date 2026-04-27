# 🚀 SDD Master Prompt Reusable Template (MVC + JDBC + Swing + Console)

Role: Senior Java Architect and SDD Specialist.

Context:
I have a Java 17+ Maven project using strict MVC and pure JDBC/DAO.  
I follow Specification-Driven Development with CONSTITUTION.md and SPEC_TEMPLATE.md as mandatory standards.  
The project must support both Swing and Console views sharing the same Service and DAO logic.

Objective:
Generate a complete SDD Documentation Pack for a new business case, preserving the same development model, architecture boundaries, and readability style as previous specs.

Input I will provide:
1. Business case description in Spanish.
2. Required business rules.
3. Domain entities and relationships.
4. Any special security requirements.
5. Optional UI requirements for Swing and Console.

Mandatory instructions:
1. Follow the structure of SPEC_TEMPLATE.md exactly.
2. Enforce CONSTITUTION.md architecture and workflow.
3. Use English for technical definitions, signatures, tables, and Gherkin.
4. Use Spanish for business context and business rule descriptions.
5. Include strict transaction flow with setAutoCommit(false) for write use cases.
6. Include SHA-256 policy when sensitive data appears.
7. Maintain total layer decoupling: View → Controller → Service → DAO → Model.
8. Reference BusinessRuleSpecTemplateTest.java style for acceptance-test mapping.
9. Do not generate implementation code yet.

Tasks:
1. Create SPEC_ProjectName.md with:
   - Business Goal
   - Hard Business Rules table
   - Acceptance Criteria in Gherkin
   - UI Requirements for Swing and Console
   - Technical Notes with artifacts mapping
   - DAO interface signatures using Connection and SQLException
   - Custom exception catalog
   - Transactional flow for the main write use case
   - Out of Scope and Approval
2. Create README snippet with:
   - Professional system overview
   - Architectural justification for MVC decoupling
   - SQL CREATE TABLE scripts with constraints and relationships
3. Add a section named Similarity Notes that explains how this spec stays consistent with previous project style for easier reading.

Output format:
Return only Markdown content for:
1. SPEC_ProjectName.md
2. README_SNIPPET_ProjectName.md
3. Optional PROMPT_NEXT_ProjectName.md for future reuse.
