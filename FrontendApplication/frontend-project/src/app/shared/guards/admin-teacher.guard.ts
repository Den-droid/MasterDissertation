import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const adminTeacherGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin() || authService.isTeacher()) {
    return true;
  }

  authService.logout();

  return router.createUrlTree(['/', 'auth', 'signin']);
};