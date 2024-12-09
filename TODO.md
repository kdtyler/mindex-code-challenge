# TODO List

## Features to implement

1. Data Transfer Object (DTO) layer 
    - Goal: Separates internal data models from API contract
    - [ ] EmployeeDTO and CompensationDTO
    - [ ] Use MapStruct to map between DTOs and entities
    - [ ] Update controllers to use DTOs
    - [ ] Update service layer to handle conversions between DTOs and entities
   

2. Soft Delete and/or Hard Delete
    - [ ] Goal: Implement a way to delete employees and compensations
    - [x] Soft Delete: Mark records as deleted but keep them in the database (COMPLETED)
    - [ ] Hard Delete: Remove records from the database
    - [ ] Very important to clarify business requirements when designing deletes.
      Soft delete preferred for audit purposes, confirm with Project Owner. 
      Confirm if hard delete is required. Inquire about retention policies
      (how long should records be maintained and/or purged based on legal requirements).
    - [ ] Determine if this CRUD app should handle hard deletes, security 
      requirements needed, etc.


3. Audit Logging
    - Goal: Implement a way to track changes to employee and compensation records
    - [ ] Log changes to a separate table or file
    - [ ] Spring AOP or Spring Data Envers


4. API Documentation
    - [ ] Goal: Document the API endpoints and data models
    - [ ] Use Swagger or Spring REST Docs
    - [ ] Discuss with Tech Lead/Team the current/preferred methods (autogeneration vs. manual)


### Note to interviewing team at Mindex:
These are some of the features I would implement to make this a more enterprise-ready
application. At the time of writing I will focus on polishing up the current version
of the application and focus on making this baseline version clean, efficient, and well-tested.
I may pick 1 or 2 of these features to implement if time allows.
