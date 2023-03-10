import tm.binding.registry.util.AuthFailureSecurityListener
import tm.binding.registry.util.AuthSuccessSecurityListener

// Place your Spring DSL code here
beans = {

  authFailureListener(AuthFailureSecurityListener.class)
  authSuccessListener(AuthSuccessSecurityListener.class)

}
