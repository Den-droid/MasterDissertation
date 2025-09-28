import { Component } from '@angular/core';
import { AuthService } from './shared/services/auth.service';
import { JWTTokenService } from './shared/services/jwt-token.service';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [RouterOutlet, CommonModule]
})
export class AppComponent {
  isAuthenticated = false;
  role = '';

  constructor(private router: Router, private readonly jwtService: JWTTokenService, private authService: AuthService) {
    jwtService.tokenChange.subscribe({
      next: (value: string | null) => {
        if (value === null || value === '') {
          this.isAuthenticated = false;
          this.role = '';
        } else {
          this.isAuthenticated = authService.isAuthenticated();
          this.role = jwtService.getRoles()[0];
        }
      }
    })
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl("/auth/signin");
  }
}
