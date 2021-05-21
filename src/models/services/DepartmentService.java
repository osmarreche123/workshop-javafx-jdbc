package models.services;

import models.dao.DaoFactory;
import models.dao.DepartmentDao;
import models.entities.Department;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    private DepartmentDao dao = DaoFactory.createDepartmentDao();

    public List<Department> findAll(){
        return dao.findAll();
    }

}
