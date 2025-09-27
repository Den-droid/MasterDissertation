import { CommonModule, Location } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AuthService } from "../../shared/services/auth.service";
import { JWTTokenService } from "../../shared/services/jwt-token.service";
import { ApiKeyDto } from "../../shared/models/auth.model";

@Component({
  selector: 'app-user-apikey',
  templateUrl: './apikey.component.html',
  imports: [FormsModule, CommonModule]
})
export class ApikeyComponent {
  apiKey: string = '';
  copyButtonText = 'Копіювати ключ';

  public constructor(private location: Location, private authService: AuthService,
    private jwtService: JWTTokenService) { }

  getNewKey() {
    this.authService.getApiKey(Number(this.jwtService.getId())).subscribe({
      next: (apiKeyDto: ApiKeyDto) => {
        this.apiKey = apiKeyDto.apiKey;
      }
    })
  }

  goBack() {
    this.location.back();
  }

  copyKey() {
    navigator.clipboard.writeText(this.apiKey).then(() => {
      this.copyButtonText = "Ключ скопійовано";
      setTimeout(() => {
        this.copyButtonText = "Копіювати ключ"
      }, 5000);
    });
  }
}
