export default {
    computed: {
       getAccountId() {
        let user_role = localStorage.getItem("userrole");
        user_role = JSON.parse(user_role);
        if (user_role) {
          return JSON.parse(user_role.userdata).accountid;
        }
       },
       /*
        hasCompanyAdminRole() {
            let user_role = localStorage.getItem("userrole");
            user_role = JSON.parse(user_role);
            if (user_role) {
              if (user_role.roles.includes("COMPANYADMIN")) {
                return true;
              }
            }
            return false;
        },
        hasSuperAdminRole() {
          let user_role = localStorage.getItem("userrole");
          user_role = JSON.parse(user_role);
          if (user_role) {
            if (user_role.roles.includes("SUPERADMIN")) {
              return true;
            }
          }
          return false;
        },
        */
      }
  };
  
