# app-deposito-moda-intima

App controle de estoque e vendas
 
## Início

  O app Depósito da Moda Íntima é um projeto desenvolvido durante a pandemia COVID-19 com o objetivo de:
  *
  **
  ***
  a pedido do Depósito da Moda Íntima, que é uma loja de vendas Atacado e Varejo de moda íntima masculina, feminina, adulto e infantil, localizado no Buraco da Gia - Centro Comercial de Comerciantes Autônomos e Informais de Vendas de Roupas e Artigos diversos, no Centro de Fortaleza-CE.
Antes da pandemia COVID-19, as vendas da loja eram realizadas 80% de modo presencial e 20% pelo app WhatsApp. Os registros das mesmas eram feitos em blocos de papéis avulsos. A loja também não dispõe de controle de estoque, nem físico e nem digital.
    Durante a pandemia e até a liberação das circulação da população, as vendas passaram a ser realizadas 100% pelo WhatsApp. Como o volume cresceu exponencialmente, as operações geraram  grande dificuldade, desgastes com clientes e perdas financeiras.
Outra observação importante é que o proprietário e seus funcionários possuem formação limitada e pouca familiaridade com uso das tecnologias
Apresentado esse contexto, surge assim a necessidade da utilização de um software que realize controle de vendas, desde o pedido até a finalização e também o estoque de produtos.
Nasce assim o APP DEPÓSITO DA MODA ÍNTIMA. Um app que oferece o serviço de estoque (manutenção de produtos) e de gestão de vendas em duas versões, cliente e loja.
Nesta disciplina estudaremos a versão LOJA. A versão loja está disponível em https://github.com/joirneto/DepositoModaIntima.git . Imagens em anexo ao final do documento.
Na versão LOJA, temos duas interfaces. As interfaces estão ligadas às permissões e funcionalidades de Administrador e usuário[vendedor]. A interface Administrador possui acesso a todas as funcionalidades do sistema. A interface usuário[vendedor] não possui acesso a edição ao banco de dados relacionados aos produtos(cadastramento, edição e exclusão) e acesso a informações de quantidades de produtos vendidos e margem de lucro.

### Pré-requisitos:

Você precisa do NodeJS e do NPM instalado em sua máquina.

```
npm install
npm run dev
```

## Layout:

Criamos o layout utilizando o Figma. Você pode encontrar o arquivo [aqui](https://www.figma.com/file/PXBVsUfwlCjNF4DiN1c4Iv/palpite-box-JN).

## Colocando em produção:

Este projeto pode ser colocado em produção utilizando o Vercel. É necessário criar as variáveis de ambiente para configurar o acesso as planilhas do Google:


## Construído com:

* [NextJS](https://nextjs.org/) -The React Framework.
* [TailwindCSS](https://tailwindcss.com/) - A utility-first CSS framework for
rapidly building custom designs.
* [Figma](https://figma.com/) - Online prototyping tool.
* [Google Sheets](https://drive.google.com) - Planilhas online do Google

## Author:

* **Joir Neto** - [LinkedIn](https://www.linkedin.com/in/joir-neto/)
