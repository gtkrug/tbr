import tm.binding.registry.UserPasswordEncoderListener
import tm.binding.registry.util.AuthFailureSecurityListener
import tm.binding.registry.util.AuthSuccessSecurityListener
import tm.binding.registry.util.TBRSecurity

// Place your Spring DSL code here
beans = {

  userPasswordEncoderListener(UserPasswordEncoderListener)
  authFailureListener(AuthFailureSecurityListener.class)
  authSuccessListener(AuthSuccessSecurityListener.class)

    // Gives methods for using in @Secured annotation.
    tbrSecurity(TBRSecurity.class)

}
