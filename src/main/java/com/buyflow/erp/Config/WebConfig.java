// spring 설정 코드 집어넣는 곳.
// 아래 코드는 구동 안해봄 예로 작성 해본 것 -> CORS 설정 
// @Configuration
// public class WebConfig {
    
//     @Bean
//     public WebMvcConfigu corsCongifigu() {

//         return new WebMvcConfigu() {
//             @Override
//             public void addCorsMappings(CorsRegistry corsRegistry) {

//                 corsRegistry.addMapping("/**")
//                     .allowedOrigins("http://localhost:8080");
//             }
//         }
//     }
// }

// JWT 인증 
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

// }