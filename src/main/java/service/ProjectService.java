package service;

import entity.Project;
import projects.dao.ProjectDao;
import projects.exceptions.DbException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class ProjectService {
    ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects()
                .stream()
                .sorted((project1, project2) -> project1.getProjectId() - project2.getProjectId())
                .collect(Collectors.toList());
    }

    public Project fetchProjectById(Integer projectId) {
        return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
    }

    public boolean modifyProjectDetails(Project project) {
        boolean isModified = projectDao.modifyProjectDetails(project);
        if (isModified) {
            throw new DbException("Project with project ID=" + project.getProjectId() + " does not exist.");
        }
        return isModified;
    }

    public void deleteProject(Integer projectId) {
        if (!projectDao.deleteProject(projectId)) {
            throw new DbException("Project with project ID=" + projectId + " does not exist.");
        }
    }
}
