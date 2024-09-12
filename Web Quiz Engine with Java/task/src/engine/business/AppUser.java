package engine.business;

import javax.persistence.*;
import java.util.List;

@Entity(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private List<Quiz> quizzes;
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private List<Completed> completedList;

    public AppUser(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public AppUser() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Completed> getCompletedList() {
        return completedList;
    }

    public void setCompletedList(List<Completed> completedList) {
        this.completedList = completedList;
    }

}
