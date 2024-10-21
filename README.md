Cloud Storage Application


Cloud Storage Application is a web-based solution for storing and managing files in the cloud. Users can create folders, upload, download, delete, and rename files, all within a user-friendly interface.

Features
User Authentication: Secure login system without roles.
File Management: Upload, download, rename, and delete files.
Folder Management: Create, rename, and delete folders.
Session Management: Integrated with Redis for session persistence.
REST API: Exposes API endpoints for file operations.
File Storage: Files are stored using MinIO.

Technology Stack
Backend:

![Spring Ecosystem](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)
![PostreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis (Session Management)](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
          
Spring Data JPA,
Spring Sessions,
Spring MVC,
MinIO SDK,


Frontend:

![HTML](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Bootstrap 5](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)
![Java Script](https://img.shields.io/badge/JavaScript-323330?style=for-the-badge&logo=javascript&logoColor=F7DF1E)
   
Thymeleaf

How to Run
1. Clone the repository: ```bash git clone https://github.com/Oldsize/cloud_storage.git ```
2. Setup Docker, PostgreSQL and Minio,
3. configure "application.properties"
4. Run Spring Boot application.
