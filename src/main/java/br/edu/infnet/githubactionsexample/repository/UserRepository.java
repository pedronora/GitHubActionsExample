package br.edu.infnet.githubactionsexample.repository;

import br.edu.infnet.githubactionsexample.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
