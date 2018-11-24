import cn.itcast.dao.CustomerDao;
import cn.itcast.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.criteria.*;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ="classpath:applicationContext.xml")
public class SpecTest {
    @Autowired
    private CustomerDao customerDao;
//单条件查询
    @Test
    public void findOneTest(){
       Specification<Customer> specification=new Specification<Customer>() {
           public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
               //1获取比较的属性值
               Path<Object> custName = root.get("custName");
               //2创建比较条件对象
               Predicate predicate = criteriaBuilder.equal(custName, "龙儿");

               //返回比较条件对象作为findOne方法的参数
               return predicate;
           }
       };
        Customer one = customerDao.findOne(specification);

        System.out.println(one);
    }
    //多条件查询

    @Test
    public void  findByMoreTest(){

        Customer one2 = customerDao.findOne(new Specification<Customer>() {
            //可以将传入的条件对象写成匿名内部类
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
               //多条件的属性有哪些
                Path<Object> custName = root.get("custName");
                Path<Object> custIndustry = root.get("custIndustry");
                //各个属性的判断有哪些
                Predicate predicate1 = criteriaBuilder.equal(custName, "龙儿");
                Predicate predicate2 = criteriaBuilder.equal(custIndustry, "圣女");
                //合并这些判断条件
                Predicate and = criteriaBuilder.and(predicate1, predicate2);


                return and;
            }
        });

        System.out.println(one2);

    }
    //模糊条件查询(需要指定属性的泛型)
    @Test
    public  void findAllTest(){
    Specification sp=new Specification() {
    public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Path path = root.get(("custName"));
        //比较条件,其中需要指定泛型
        Predicate predicate = criteriaBuilder.like(path.as(String.class), "龙儿%");
        return predicate;
    }
};
       /* List list = customerDao.findAll(sp);
        for (Object like : list) {
            System.out.println(like);
        }*/

       //降序查询

        Sort sort=new Sort(Sort.Direction.DESC,"custId");
        List list = customerDao.findAll(sp, sort);
        for (Object two : list) {
            System.out.println(two);
        }
    }

    //分页查询

    @Test
    public void findByPage(){

        Specification spec=null;
            //建立分页对象
        Pageable pageable=new PageRequest(0,2);

        Page<Customer> page = customerDao.findAll(spec, pageable);

        System.out.println(page.getTotalElements());
        System.out.println(page.getContent());


    }

    }
