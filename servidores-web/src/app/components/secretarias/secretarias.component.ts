import { Component, OnInit, inject } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators
} from '@angular/forms';

import {
  SecretariaService,
  SecretariaRequest
} from '../../services/secretaria.service';

import { Secretaria } from '../../models/secretaria';

@Component({
  selector: 'app-secretarias',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './secretarias.component.html',
  styleUrl: './secretarias.component.scss'
})
export class SecretariasComponent implements OnInit {

  private readonly secretariaService = inject(SecretariaService);
  private readonly fb = inject(FormBuilder);

  secretarias: Secretaria[] = [];

  editandoId: number | null = null;

  mensagem = '';
  erro = '';

  secretariaForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    sigla: ['', Validators.required]
  });

  ngOnInit(): void {
    this.carregarSecretarias();
  }

  carregarSecretarias(): void {
    this.secretariaService.listar().subscribe({
      next: (secretarias) => {
        this.secretarias = secretarias;
      },
      error: (erro) => {
        console.error(erro);
        this.erro = 'Não foi possível carregar as secretarias.';
      }
    });
  }

  salvarSecretaria(): void {

    this.mensagem = '';
    this.erro = '';

    if (this.secretariaForm.invalid) {
      this.secretariaForm.markAllAsTouched();
      return;
    }

    const request: SecretariaRequest =
      this.secretariaForm.getRawValue();

    if (this.editandoId === null) {

      this.secretariaService.criar(request).subscribe({
        next: () => {
          this.mensagem = 'Secretaria cadastrada com sucesso.';
          this.limparFormulario();
          this.carregarSecretarias();
        },
        error: (erro) => {
          this.erro =
            erro.error?.message ||
            'Erro ao cadastrar secretaria.';
        }
      });

      return;
    }

    this.secretariaService
      .atualizar(this.editandoId, request)
      .subscribe({
        next: () => {
          this.mensagem = 'Secretaria atualizada com sucesso.';
          this.limparFormulario();
          this.carregarSecretarias();
        },
        error: (erro) => {
          this.erro =
            erro.error?.message ||
            'Erro ao atualizar secretaria.';
        }
      });
  }

  editarSecretaria(secretaria: Secretaria): void {

    this.editandoId = secretaria.id;

    this.secretariaForm.patchValue({
      nome: secretaria.nome,
      sigla: secretaria.sigla
    });

    this.mensagem = '';
    this.erro = '';
  }

  excluirSecretaria(secretaria: Secretaria): void {

    if (!confirm(
      `Deseja excluir a secretaria ${secretaria.nome}?`
    )) {
      return;
    }

    this.secretariaService
      .excluir(secretaria.id)
      .subscribe({
        next: () => {
          this.mensagem =
            'Secretaria excluída com sucesso.';

          this.erro = '';

          this.carregarSecretarias();
        },
        error: (erro) => {
          this.mensagem = '';

          this.erro =
            erro.error?.message ||
            'Erro ao excluir secretaria.';
        }
      });
  }

  limparFormulario(): void {

    this.editandoId = null;

    this.secretariaForm.reset({
      nome: '',
      sigla: ''
    });
  }
}
