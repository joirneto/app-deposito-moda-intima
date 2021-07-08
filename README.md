# app-deposito-moda-intima

App de gestão estoque e vendas de produtos
 
## Início

  O app Depósito da Moda Íntima é um projeto desenvolvido durante a pandemia COVID-19 com o objetivo de:
  * Gestão de Estoque de produtos
  * Gestão de Vendas
  * Vendas on-line
  
  Com o enfrentamento da pandemia COVID-19 e os impactos na econômia, a receita proveniente de vendas presenciais diminuiram circunstancialmente, crescendo a demanda por vendas online. Com isso, recebi um desafio, a pedido do Depósito da Moda Íntima, que é uma loja de vendas Atacado e Varejo de moda íntima masculina, feminina, adulto e infantil, de apresentar uma solução, já que o mesmo não possuia de controle de estoque, nem físico e nem digital.
  
   localizado no Buraco da Gia - Centro Comercial de Comerciantes Autônomos e Informais de Vendas de Roupas e Artigos diversos, no Centro de Fortaleza-CE.

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
