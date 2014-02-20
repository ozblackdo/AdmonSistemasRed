/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JPA.Entidades_Controllers;

import JPA.Entidades.Area;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import JPA.Entidades.Empleado;
import java.util.ArrayList;
import java.util.List;
import JPA.Entidades.Depto;
import JPA.Entidades_Controllers.exceptions.IllegalOrphanException;
import JPA.Entidades_Controllers.exceptions.NonexistentEntityException;
import JPA.Entidades_Controllers.exceptions.PreexistingEntityException;
import JPA.Entidades_Controllers.exceptions.RollbackFailureException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

/**
 *
 * @author DELL
 */
public class AreaJpaController implements Serializable {

   public AreaJpaController() {
    }
    
    private EntityManagerFactory emf = null;

    private EntityManager getEntityManager() {
        emf = Persistence.createEntityManagerFactory("It_ITILPU");
        return emf.createEntityManager();
    }

    public void create(Area area) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (area.getEmpleadoList() == null) {
            area.setEmpleadoList(new ArrayList<Empleado>());
        }
        if (area.getDeptoList() == null) {
            area.setDeptoList(new ArrayList<Depto>());
        }
        EntityManager em = null;
        try {
           
            em = getEntityManager();
            List<Empleado> attachedEmpleadoList = new ArrayList<Empleado>();
            for (Empleado empleadoListEmpleadoToAttach : area.getEmpleadoList()) {
                empleadoListEmpleadoToAttach = em.getReference(empleadoListEmpleadoToAttach.getClass(), empleadoListEmpleadoToAttach.getEmpNoEmpleado());
                attachedEmpleadoList.add(empleadoListEmpleadoToAttach);
            }
            area.setEmpleadoList(attachedEmpleadoList);
            List<Depto> attachedDeptoList = new ArrayList<Depto>();
            for (Depto deptoListDeptoToAttach : area.getDeptoList()) {
                deptoListDeptoToAttach = em.getReference(deptoListDeptoToAttach.getClass(), deptoListDeptoToAttach.getDepDepartamento());
                attachedDeptoList.add(deptoListDeptoToAttach);
            }
            area.setDeptoList(attachedDeptoList);
            em.persist(area);
            for (Empleado empleadoListEmpleado : area.getEmpleadoList()) {
                Area oldAreaareidAreaOfEmpleadoListEmpleado = empleadoListEmpleado.getAreaareidArea();
                empleadoListEmpleado.setAreaareidArea(area);
                empleadoListEmpleado = em.merge(empleadoListEmpleado);
                if (oldAreaareidAreaOfEmpleadoListEmpleado != null) {
                    oldAreaareidAreaOfEmpleadoListEmpleado.getEmpleadoList().remove(empleadoListEmpleado);
                    oldAreaareidAreaOfEmpleadoListEmpleado = em.merge(oldAreaareidAreaOfEmpleadoListEmpleado);
                }
            }
            for (Depto deptoListDepto : area.getDeptoList()) {
                Area oldAreaareidAreaOfDeptoListDepto = deptoListDepto.getAreaareidArea();
                deptoListDepto.setAreaareidArea(area);
                deptoListDepto = em.merge(deptoListDepto);
                if (oldAreaareidAreaOfDeptoListDepto != null) {
                    oldAreaareidAreaOfDeptoListDepto.getDeptoList().remove(deptoListDepto);
                    oldAreaareidAreaOfDeptoListDepto = em.merge(oldAreaareidAreaOfDeptoListDepto);
                }
            }
            
        } catch (Exception ex) {
            try {
                
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findArea(area.getAreidArea()) != null) {
                throw new PreexistingEntityException("Area " + area + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Area area) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            Area persistentArea = em.find(Area.class, area.getAreidArea());
            List<Empleado> empleadoListOld = persistentArea.getEmpleadoList();
            List<Empleado> empleadoListNew = area.getEmpleadoList();
            List<Depto> deptoListOld = persistentArea.getDeptoList();
            List<Depto> deptoListNew = area.getDeptoList();
            List<String> illegalOrphanMessages = null;
            for (Empleado empleadoListOldEmpleado : empleadoListOld) {
                if (!empleadoListNew.contains(empleadoListOldEmpleado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Empleado " + empleadoListOldEmpleado + " since its areaareidArea field is not nullable.");
                }
            }
            for (Depto deptoListOldDepto : deptoListOld) {
                if (!deptoListNew.contains(deptoListOldDepto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Depto " + deptoListOldDepto + " since its areaareidArea field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Empleado> attachedEmpleadoListNew = new ArrayList<Empleado>();
            for (Empleado empleadoListNewEmpleadoToAttach : empleadoListNew) {
                empleadoListNewEmpleadoToAttach = em.getReference(empleadoListNewEmpleadoToAttach.getClass(), empleadoListNewEmpleadoToAttach.getEmpNoEmpleado());
                attachedEmpleadoListNew.add(empleadoListNewEmpleadoToAttach);
            }
            empleadoListNew = attachedEmpleadoListNew;
            area.setEmpleadoList(empleadoListNew);
            List<Depto> attachedDeptoListNew = new ArrayList<Depto>();
            for (Depto deptoListNewDeptoToAttach : deptoListNew) {
                deptoListNewDeptoToAttach = em.getReference(deptoListNewDeptoToAttach.getClass(), deptoListNewDeptoToAttach.getDepDepartamento());
                attachedDeptoListNew.add(deptoListNewDeptoToAttach);
            }
            deptoListNew = attachedDeptoListNew;
            area.setDeptoList(deptoListNew);
            area = em.merge(area);
            for (Empleado empleadoListNewEmpleado : empleadoListNew) {
                if (!empleadoListOld.contains(empleadoListNewEmpleado)) {
                    Area oldAreaareidAreaOfEmpleadoListNewEmpleado = empleadoListNewEmpleado.getAreaareidArea();
                    empleadoListNewEmpleado.setAreaareidArea(area);
                    empleadoListNewEmpleado = em.merge(empleadoListNewEmpleado);
                    if (oldAreaareidAreaOfEmpleadoListNewEmpleado != null && !oldAreaareidAreaOfEmpleadoListNewEmpleado.equals(area)) {
                        oldAreaareidAreaOfEmpleadoListNewEmpleado.getEmpleadoList().remove(empleadoListNewEmpleado);
                        oldAreaareidAreaOfEmpleadoListNewEmpleado = em.merge(oldAreaareidAreaOfEmpleadoListNewEmpleado);
                    }
                }
            }
            for (Depto deptoListNewDepto : deptoListNew) {
                if (!deptoListOld.contains(deptoListNewDepto)) {
                    Area oldAreaareidAreaOfDeptoListNewDepto = deptoListNewDepto.getAreaareidArea();
                    deptoListNewDepto.setAreaareidArea(area);
                    deptoListNewDepto = em.merge(deptoListNewDepto);
                    if (oldAreaareidAreaOfDeptoListNewDepto != null && !oldAreaareidAreaOfDeptoListNewDepto.equals(area)) {
                        oldAreaareidAreaOfDeptoListNewDepto.getDeptoList().remove(deptoListNewDepto);
                        oldAreaareidAreaOfDeptoListNewDepto = em.merge(oldAreaareidAreaOfDeptoListNewDepto);
                    }
                }
            }
            
        } catch (Exception ex) {
            try {
               
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = area.getAreidArea();
                if (findArea(id) == null) {
                    throw new NonexistentEntityException("The area with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            Area area;
            try {
                area = em.getReference(Area.class, id);
                area.getAreidArea();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The area with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Empleado> empleadoListOrphanCheck = area.getEmpleadoList();
            for (Empleado empleadoListOrphanCheckEmpleado : empleadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Area (" + area + ") cannot be destroyed since the Empleado " + empleadoListOrphanCheckEmpleado + " in its empleadoList field has a non-nullable areaareidArea field.");
            }
            List<Depto> deptoListOrphanCheck = area.getDeptoList();
            for (Depto deptoListOrphanCheckDepto : deptoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Area (" + area + ") cannot be destroyed since the Depto " + deptoListOrphanCheckDepto + " in its deptoList field has a non-nullable areaareidArea field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(area);
            
        } catch (Exception ex) {
            try {
                
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Area> findAreaEntities() {
        return findAreaEntities(true, -1, -1);
    }

    public List<Area> findAreaEntities(int maxResults, int firstResult) {
        return findAreaEntities(false, maxResults, firstResult);
    }

    private List<Area> findAreaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Area.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Area findArea(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Area.class, id);
        } finally {
            em.close();
        }
    }

    public int getAreaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Area> rt = cq.from(Area.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
   
    
}
