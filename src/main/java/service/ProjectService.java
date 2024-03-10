package service;

import entity.Project;
import projects.dao.ProjectDao;

public class ProjectService {
    ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }
}
