package models.dao.impl;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import models.dao.DepartmentDao;
import models.dao.SellerDao;
import models.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {

        PreparedStatement st = null;

        try {

        st = conn.prepareStatement("INSERT INTO department "
        + "(Name) "
        + "VALUES "
        + "(?)", Statement.RETURN_GENERATED_KEYS);

        st.setString(1, obj.getName());

        int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){

             ResultSet rs = st.getGeneratedKeys();

             if(rs.next()){

                 int id = rs.getInt(1);
                    obj.setId(id);

             }
             DB.closeResultSet(rs);

         }
            else {
               throw new DbException("Error to insert, No rows Afected ");
            }

        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Department obj) {

        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("UPDATE department "
            + "SET Name = ? "
            + "WHERE Id = ?");

            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());

            st.executeUpdate();
        }

        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        finally {
            DB.closeStatement(st);
        }


    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("DELETE FROM department "
            + "WHERE Id = ? ", Statement.RETURN_GENERATED_KEYS);

            st.setInt(1, id);
            int rowsAfected = st.executeUpdate();

            if (rowsAfected > 0){
                System.out.println("Remove query has ben sucessfull !");
            }

                            }

        catch (SQLException e) {
           throw new DbIntegrityException(e.getMessage());
        }

        finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM department " +
                    "WHERE Id = (?) ");

            st.setInt(1, id);

            rs = st.executeQuery();

            if (rs.next()){

                Department dep = instanciateDepartment(rs);
                return dep;

            }
            return null;
        }
         catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }



    }

    @Override
    public List<Department> findAll() {

        List<Department> listDep = new ArrayList<>();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM department");
            rs = st.executeQuery();

            while (rs.next()){
                Department dep = instanciateDepartment(rs);
                listDep.add(dep);

            }

            return listDep;
        }


        catch (SQLException e) {
            throw  new DbException(e.getMessage());
        }

        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }



    }


    public Department instanciateDepartment(ResultSet rs) throws SQLException {

        Department dep = new Department();

        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));

        return dep;
    }
}
