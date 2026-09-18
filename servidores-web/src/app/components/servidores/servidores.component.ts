import { Component, OnInit, inject } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators
} from '@angular/forms';

import {
  ServidorService,
  ServidorRequest
} from '../../services/servidor.service';

import { Servidor } from '../../models/servidor';
import { ServidorHistorico } from '../../models/servidor-historico';

import { SecretariaService } from '../../services/secretaria.service';
import { Secretaria } from '../../models/secretaria';

@Component({
  selector: 'app-servidores',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './servidores.component.html',
  styleUrl: './servidores.component.scss'
})
export class ServidoresComponent implements OnInit {

  private readonly servidorService = inject(ServidorService);
  private readonly secretariaService = inject(SecretariaService);
  private readonly fb = inject(FormBuilder);

  servidores: Servidor[] = [];
  secretarias: Secretaria[] = [];

  servidorSelecionado: Servidor | null = null;
  historicoSelecionado: ServidorHistorico[] = [];

  editandoId: number | null = null;

  mensagem = '';
  erro = '';

  servidorForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    dataNascimento: ['', Validators.required],
    secretariaId: [0, [
      Validators.required,
      Validators.min(1)
    ]]
  });

  ngOnInit(): void {
    this.carregarServidores();
    this.carregarSecretarias();
  }

  carregarServidores(): void {
    this.servidorService.listar().subscribe({
      next: (servidores) => {
        this.servidores = servidores;
      },
      error: (erro) => {
        console.error(erro);
        this.erro =
          'Não foi possível carregar os servidores.';
      }
    });
  }

  carregarSecretarias(): void {
    this.secretariaService.listar().subscribe({
      next: (secretarias) => {
        this.secretarias = secretarias;
      },
      error: (erro) => {
        console.error(erro);
        this.erro =
          'Não foi possível carregar as secretarias.';
      }
    });
  }

  salvarServidor(): void {

    if (this.servidorForm.invalid) {
      this.servidorForm.markAllAsTouched();
      return;
    }

    const request: ServidorRequest =
      this.servidorForm.getRawValue();

    if (this.editandoId === null) {

      this.servidorService.criar(request).subscribe({
        next: () => {
          this.mensagem = 'Servidor cadastrado com sucesso.';
          this.erro = '';
          this.limparFormulario();
          this.carregarServidores();
        },
        error: (erro) => {
          this.mensagem = '';
          this.erro =
            erro.error?.message || 'Erro ao cadastrar servidor.';
        }
      });

    } else {

      this.servidorService
        .atualizar(this.editandoId, request)
        .subscribe({
          next: () => {
            this.mensagem = 'Servidor atualizado com sucesso.';
            this.erro = '';
            this.limparFormulario();
            this.carregarServidores();
          },
          error: (erro) => {
            this.mensagem = '';
            this.erro =
              erro.error?.message || 'Erro ao atualizar servidor.';
          }
        });
    }
  }

  editarServidor(servidor: Servidor): void {
    this.editandoId = servidor.id;

    this.servidorForm.patchValue({
      nome: servidor.nome,
      email: servidor.email,
      dataNascimento: servidor.dataNascimento,
      secretariaId: servidor.secretaria.id
    });
  }

  excluirServidor(servidor: Servidor): void {

    if (!confirm(`Deseja excluir o servidor ${servidor.nome}?`)) {
      return;
    }

    this.servidorService.excluir(servidor.id).subscribe({
      next: () => {
        this.mensagem = 'Servidor excluído com sucesso.';
        this.erro = '';
        this.carregarServidores();
      },
      error: (erro) => {
        this.mensagem = '';
        this.erro =
          erro.error?.message || 'Erro ao excluir servidor.';
      }
    });
  }

  limparFormulario(): void {
    this.editandoId = null;

    this.servidorForm.reset({
      nome: '',
      email: '',
      dataNascimento: '',
      secretariaId: 0
    });
  }

  mostrarHistorico(servidor: Servidor): void {
    this.servidorSelecionado = servidor;

    this.servidorService.historico(servidor.id).subscribe({
      next: (historico) => {
        this.historicoSelecionado = historico;
      },
      error: (erro) => {
        console.error('Erro ao buscar histórico:', erro);
        this.historicoSelecionado = [];
      }
    });
  }
}
