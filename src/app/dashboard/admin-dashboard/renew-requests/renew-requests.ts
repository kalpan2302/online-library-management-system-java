import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface RenewRequest {
  userEmail: string;
  issuedBook: string;
  issuedBy: string;
  issueDate: string;
  scheduledReturnDate: string;
}

@Component({
  selector: 'app-renew-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renew-requests.html'
})
export class RenewRequests {
  requests: RenewRequest[] = [
    {
      userEmail: 'user1@gmail.com',
      issuedBook: 'Thinking with type',
      issuedBy: 'Admin 1_name',
      issueDate: '12-08-2025',
      scheduledReturnDate: '22-08-2025'
    },
    {
      userEmail: 'user2@gmail.com',
      issuedBook: 'Thinking with type',
      issuedBy: 'Admin 2_name',
      issueDate: '12-08-2025',
      scheduledReturnDate: '22-08-2025'
    },
    {
      userEmail: 'user3@gmail.com',
      issuedBook: 'Thinking with type',
      issuedBy: 'Admin 2_name',
      issueDate: '12-08-2025',
      scheduledReturnDate: '22-08-2025'
    }
  ];

  // Example methods for action icons
  acceptRenew(request: RenewRequest) {
    // Handle accept logic
  }

  declineRenew(request: RenewRequest) {
    // Handle decline logic
  }
}
