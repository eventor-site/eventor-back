package com.eventorback.global.filter;// package com.sikyeojoback.global.filter;
//
// import java.io.IOException;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.NonNull;
//
// @Component
// public class IpAddressFilter extends OncePerRequestFilter {
// 	@Value("${remote-addr}")
// 	private String remoteAddr;
//
// 	@Override
// 	protected void doFilterInternal(
// 		@NonNull HttpServletRequest request,
// 		@NonNull HttpServletResponse response,
// 		@NonNull FilterChain filterChain) throws ServletException, IOException {
// 		String requestRemoteAddress = request.getRemoteAddr();
// 		if ("/users/info".equals(request.getRequestURI()) && !remoteAddr.equals(requestRemoteAddress)) {
// 			response.sendError(HttpServletResponse.SC_FORBIDDEN, "해당 IP는 접근 권한이 없습니다.");
// 			return;
// 		}
//
// 		filterChain.doFilter(request, response);
// 	}
//
// 	@Override
// 	@NonNull
// 	protected String getAlreadyFilteredAttributeName() {
// 		return this.getClass().getName() + ".FILTERED";
// 	}
// }
