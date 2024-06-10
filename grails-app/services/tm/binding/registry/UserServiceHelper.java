package tm.binding.registry;

import grails.gorm.transactions.Transactional;
import org.gtri.fj.data.List;
import org.json.JSONArray;

@Transactional
public class UserServiceHelper {

    public void insertOrUpdateHelper(
            final String username,
            final String nameFamily,
            final String nameGiven,
            final String contactEmail,
            final List<Role> roleList) {

        User.withTransactionHelper( () -> {

            User user = User.findByUsernameHelper(username).orSome(new User());
            user.setUsername(username);
            user.setNameFamily(nameFamily);
            user.setNameGiven(nameGiven);
            user.setName(nameGiven + ", " + nameFamily);
            user.setContactEmail(contactEmail);

            user.setRoleArrayJson(new JSONArray(roleList.map(role -> role.getValue())).toString());

            user.saveAndFlushHelper();
        });
    }
}
