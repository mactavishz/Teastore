# Contributions

Contributions are divided into two parts:

- middle contributions: made till base-line report
- final contributions: made till final report

_Note: The contributions presented here are exactly the same as those presented in the report appendix and are merely summarized here._

## 1. Middle Contributions (SUE branch, till base line report)

### Chao Zhan

- Planned, researched ideas for the refactoring.
- Organized the project, see the [Issue board](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/boards)
- Refactored the Registry service, experimented with Open Liberty and verify the refactoring approach. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/2).
- Refactored the Recommender service, which serves as an exemplar for the refactoring of other services. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/3)
- Provided documentation for the refactoring. See this [Wiki page](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/wikis/TeaStore-Refactoring-Guide)
- Added monitoring to the original project settings. Implemented benchmarking scripts using K6. Collected benchmark data in the production environment. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5) and related issues.
- Prepared slides for the presentation. Link to the Google slides: [Here](https://docs.google.com/presentation/d/12jpbkOXEshWssyjN2TB1FapAtLO9VR0k4rvX4YWHgJU/edit?usp=sharing).
- WIP: refactoring the WebUI service. See this [Issue](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/3) and related [Branch](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/tree/3-refactor-webui-service-possibly-rewrite-using-modern-frontend-tech?ref_type=heads).
- Report: wrote Section on benchmarking, did general editing on sections about refactor performance and state and issues

### Rui Zhao

- Contributed ideas for refactoring.
- Participated in organizing the project, see the [Issue board](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/boards).
- Participated in refactoring Persistence component. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/4).
- Moved the original project setup to kubernetes, using kustomization to manage kubernetes resources. [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/1).
- Managed and debugged the kubernetes setup and tailored the resources to meet our develop/test needs. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5).
- Added Prometheus + Grafana to the kubernetes setup to gain the observability. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5).
- Wrote documentation for kubernetes setup. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5).
- Prepared slides for the presentation and present. Link to the Google slides: [Here](https://docs.google.com/presentation/d/12jpbkOXEshWssyjN2TB1FapAtLO9VR0k4rvX4YWHgJU/edit?usp=sharing).
- Report: wrote Section on refactoring proposals, depicted new architecture outlook.

### Hassan Bassiouny

- Refactored the authenticator web service by removing the load balancer enhancing its cloud-native capabilities. This involved enabling communication with other services, such as Persistence, via REST interfaces.
  [Auth](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/7)
- Currently refactoring the image web service to address its bottleneck in the teastore offering a new server side solution. To improve the image web service, refactor its image decoding, storage, and delivery processes to eliminate blocking operations. This enhancement aims to boost performance and scalability by optimizing how images are processed and distributed across the platform. [Image](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/27)
- Report: wrote Section on introduction

### Siyuan Tu

- Refactored the image web service by removing the load balancer to enhance its cloud-native capabilities. This involved maintaining communication with other services, such as Persistence and WebUI, via REST interfaces. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/8).
- Currently working on refactoring the image web service: Figure out how to use the Content Delivery Network (CDN) or self-hosted static content server such as Nginx to host pre-prepared images.

### Mier Barsanjy (Missing)

- Refactored the persistence service by improving the caching mechanism using Caffeine. This enhancement aimed to reduce the response time and improve the overall efficiency of database operations. [Issue](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/9)
- WIP: Will assist in the refactoring of the Image Web service as the service might need involvement from the Persistence service.
- Report: wrote Section on introduction and Section on conclusion (using generative AI)

## 2. Final Contributions (main branch, till final report)

### Chao Zhan

- Planned, researched ideas for the refactoring.
- Organized the project, see the [Issue board](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/boards)
- Refactored the Registry service, experimented with Open Liberty and verify the refactoring approach. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/2).
- Refactored the Recommender service, which serves as an exemplar for the refactoring of other services. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/3)
- Provided documentation for the refactoring. See this [Wiki page](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/wikis/TeaStore-Refactoring-Guide)
- Added monitoring to the original project settings. Implemented benchmarking scripts using K6. Collected benchmark data in the production environment. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5) and related issues.
- Prepared slides for the presentation. Link to the Google slides: [Here](https://docs.google.com/presentation/d/12jpbkOXEshWssyjN2TB1FapAtLO9VR0k4rvX4YWHgJU/edit?usp=sharing).
- Participated in the refactoring of the Image service. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/12).
- Participated in the refactoring of the Auth service. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/6).
- Refactored the Persistence service using Open Liberty, separated DBGenerator from the Persistence service as a utility program for generating the Database. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/4)
- Refactored the WebUI service with Open Liberty. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/17).
- Refactored the WebUI service with Node.js (Remix + React.js). See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/11).
- Integrated all the refactored services, cleaned up the project, added necessary scripts for the development and build. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/13).
- Implemented scripts for generating charts and tables used in the final report. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/16).
- Collected benchmark data for the refactored version in the production environment. See this [issue](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/24) and related commits.
- Prepare documentation for the project and the final report. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/15).
- Final report: wrote Section 3, 4.3, 4.5, 6, co-write 4.1, 4.2, 4.4, did general editing on other sections.

### Rui Zhao

- Researched and contributed ideas for refactoring.
- Participated in organizing the project, see the [Issue board](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/boards).
- Participated in refactoring Persistence component. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/4).
- Moved the original project setup to kubernetes, using kustomization to manage kubernetes resources. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/1).
- Managed and debugged the kubernetes setup and tailored the resources to meet our develop/test needs. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5).
- Added Prometheus + Grafana to the kubernetes setup to gain the observability for SUE. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5).
- Wrote documentation for kubernetes setup. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/5).
- Prepared slides for the presentation and present. Link to the Google slides: [Here](https://docs.google.com/presentation/d/12jpbkOXEshWssyjN2TB1FapAtLO9VR0k4rvX4YWHgJU/edit?usp=sharing).
- Participated in refactoring the WebUI service with Node.js (Remix + React.js). See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/11).
- Created kubernetes configurations for refactored Teastore and migrate the refactored TeaStore from development environmnet(docker-compose) onto production environmnet(Kubernetes). See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/14).
- Build the observability suite for refactored Teastore contains Prometheus, Grafana, Metrics-server, Kube-state-metrics and their integrations and pre-configuration. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/14).
- Prepare documentation for the project and the final report. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/merge_requests/15).
- Final Report: co-wrote Section 2, 6, instruct AI on section 7, depicted new architecture outlook.

### Hassan Bassiouny

- Refactored the authenticator web service by removing the load balancer enhancing its cloud-native capabilities. This involved enabling communication with other services, such as Persistence, via REST interfaces.
  [Auth](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/7)
- Currently refactoring the image web service to address its bottleneck in the teastore offering a new server side solution. To improve the image web service, refactor its image decoding, storage, and delivery processes to eliminate blocking operations. This enhancement aims to boost performance and scalability by optimizing how images are processed and distributed across the platform. [Image](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/27)
- Report: wrote Section 1

### Siyuan Tu

- Refactored the image web service by removing the load balancer to enhance its cloud-native capabilities. This involved maintaining communication with other services, such as Persistence and WebUI, via REST interfaces. See this [MR](https://git.tu-berlin.de/mactavishz/cnae-2024-project/-/issues/8).
- Currently working on refactoring the image web service: Figure out how to use the Content Delivery Network (CDN) or self-hosted static content server such as Nginx to host pre-prepared images.
