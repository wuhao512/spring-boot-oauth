package com.alex.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alex.entity.Account;
import com.alex.repository.AccountRepository;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	// 查询用户使用
	@Autowired
	AccountRepository accountRepository;

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).
		permitAll().anyRequest().authenticated()
		.and().httpBasic()
		.and().csrf().disable();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
				// 通过用户名获取用户信息
				Account account = accountRepository.findByName(name);
				if (account != null) {
					// 创建spring security安全用户
					User user = new User(account.getName(), account.getPassword(),
							AuthorityUtils.createAuthorityList(account.getRoles()));
					return user;
				} else {
					throw new UsernameNotFoundException("用户[" + name + "]不存在");
				}
			}
		};

	}
}
