# GitHub Actions Workflows README

This directory contains GitHub Actions workflows that automate various tasks for the Vireo project.

## Available Workflows

### `deploy_docker.yml`

This workflow is responsible for deploying Docker images to a self-hosted server.

#### Purpose

The `deploy_docker.yml` workflow automates the process of pulling and running a specified Docker image on the `msel-vireo03.mse.jhu.edu` server. This is primarily used for deploying new versions of the Vireo application to either the staging or production environment.

#### Trigger

This workflow is triggered manually using `workflow_dispatch`. This allows for controlled deployments.

#### Inputs

When manually triggering the workflow, you must provide the following inputs:

*   **`images`**:
    *   **Description**: Specifies which Docker image to deploy.
    *   **Type**: `choice`
    *   **Options**:
        *   `vireo`: The main Vireo application image.
        *   `vireo-dev`: The development version of the Vireo application image.
    *   **Default**: `vireo-dev`
    *   **Required**: `true`
*   **`environment`**:
    *   **Description**: Specifies the target environment for the deployment (e.g., stage or prod).
    *   **Type**: `environment`
    *   **Default**: `stage`
    *   **Required**: `true`

#### Steps

1.  **`executing remote ssh commands as jhu-devops using secret`**:
    *   This step connects to the `msel-vireo03.mse.jhu.edu` server via SSH as the `jhu-devops` user.
    *   It uses the `JHU_DEVOPS_KEY` secret for authentication.
    *   It executes the following commands on the remote server:
        *   `docker pull ghcr.io/jhu-sheridan-libraries/${{ inputs.images }}:4.2.10`: Pulls the specified Docker image (e.g., `vireo` or `vireo-dev`) with the tag `4.2.10` from the GitHub Container Registry.
        *   `docker run -d -p 9000:9000 ghcr.io/jhu-sheridan-libraries/${{ inputs.images }}:4.2.10`: Runs the pulled Docker image in detached mode (`-d`) and maps port 9000 on the host to port 9000 in the container.

#### Server

*   **Host**: `msel-vireo03.mse.jhu.edu`
*   **User**: `jhu-devops`

#### Secrets

*   **`JHU_DEVOPS_KEY`**: This secret contains the SSH private key for the `jhu-devops` user on the deployment server.

#### Self-Hosted Runner

This workflow runs on a self-hosted runner, indicated by `runs-on: [self-hosted]`.

## How to Use

1.  Go to the "Actions" tab in your GitHub repository.
2.  Select the "Deploy" workflow.
3.  Click the "Run workflow" button.
4.  Choose the desired `images` and `environment` from the dropdown menus.
5.  Click "Run workflow" to start the deployment.