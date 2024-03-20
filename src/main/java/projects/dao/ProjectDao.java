package projects.dao;

import com.mysql.cj.jdbc.ConnectionImpl;
import entity.Category;
import entity.Material;
import entity.Project;
import entity.Step;
import projects.exceptions.DbException;
import provided.util.DaoBase;

import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectDao extends DaoBase {

    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";


    public Optional<Project> fetchProjectById(Integer projectId) {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
        try (Connection connection = DbConnection.getConnection()) {
            startTransaction(connection);
            try {
                Project project = null;
                try {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        setParameter(preparedStatement, 1, projectId, Integer.class);
                        try (ResultSet rs = preparedStatement.executeQuery()) {
                            if (rs.next()) {
                                project = extract(rs, Project.class);
                            }
                        }
                    }
                    if (Objects.nonNull(project)) {
                        project.getMaterials().addAll(fetchMaterialsByProjectId(connection, projectId));
                        project.getSteps().addAll(fetchStepsByProjectId(connection, projectId));
                        project.getCategories().addAll(fetchCategoriesByProjectId(connection, projectId));
                    }
                    commitTransaction(connection);
                    return Optional.ofNullable(project);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                rollbackTransaction(connection);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Project insertProject(Project project) {
        String sql = ""
                + "INSERT INTO " + PROJECT_TABLE
                + " (project_name, estimated_hours, actual_hours, difficulty, notes) "
                + "VALUES "
                + "(?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);
                stmt.executeUpdate();
                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);//DAO base
                project.setProjectId(projectId);
                return project;
            } catch (Exception e) {
                rollbackTransaction(conn);//DAO base
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public List<Project> fetchAllProjects() {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " order by project_id DESC;";
        try (Connection connection = DbConnection.getConnection()) {
            startTransaction(connection);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    List<Project> projects = new LinkedList<>();
                    while (rs.next()) {
                        Project project = new Project();
                        project.setProjectId(rs.getInt("project_id"));
                        project.setProjectName(rs.getString("project_name"));
                        project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                        project.setActualHours(rs.getBigDecimal("actual_hours"));
                        project.setDifficulty(rs.getInt("difficulty"));
                        project.setNotes(rs.getString("notes"));
                        projects.add(project);
                    }
                    return projects;
                }
            } catch (Exception e) {
                rollbackTransaction(connection);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Material> fetchMaterialsByProjectId(Connection connection, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, 1, projectId, Integer.class);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Material> materials = new LinkedList<>();
                while (resultSet.next()) {
                    materials.add(extract(resultSet, Material.class));
                }
                return materials;
            }
        }
    }

    private List<Category> fetchCategoriesByProjectId(Connection connection, Integer projectId) throws SQLException {
        String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c " +
                "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) " +
                " WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, 1, projectId, Integer.class);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Category> categories = new LinkedList<>();
                while (resultSet.next()) {
                    categories.add(extract(resultSet, Category.class));
                }
                return categories;
            }
        }
    }

    ;

    private List<Step> fetchStepsByProjectId(Connection connection, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, 1, projectId, Integer.class);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Step> steps = new LinkedList<>();
                while (resultSet.next()) {
                    steps.add(extract(resultSet, Step.class));
                }
                return steps;
            }
        }
    }

    ;

    public boolean modifyProjectDetails(Project project) {
        String sql = ""
                + "UPDATE " + PROJECT_TABLE + " SET "
                + "project_name = ?, "
                + "estimated_hours = ?,"
                + "actual_hours = ?,"
                + "difficulty = ?,"
                + "notes = ? "
                + "WHERE project_id = ?";
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);
                setParameter(stmt, 6, project.getProjectId(), Integer.class);
                boolean modified = stmt.executeUpdate() == 1;
                commitTransaction(conn);
                return modified;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public boolean deleteProject(Integer projectId) {
        String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, projectId, Integer.class);
                int rowsAffected = stmt.executeUpdate();
                boolean deleted = rowsAffected > 0;
                if (rowsAffected == 0) {
                    return false;
                }
                commitTransaction(conn);
                return deleted;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
}