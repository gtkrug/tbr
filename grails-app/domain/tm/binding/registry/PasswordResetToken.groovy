package tm.binding.registry

import java.time.LocalDateTime

class PasswordResetToken {

    public static final int EXPIRATION = 24

    String token
    LocalDateTime requestDateTime
    LocalDateTime expireDateTime

    static belongsTo = [user:User]

    static constraints = {
        token nullable: true, length: 36
        requestDateTime nullable: true
        expireDateTime nullable: true
    }
}
