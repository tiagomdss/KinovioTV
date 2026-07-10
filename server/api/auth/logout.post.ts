import { defineEventHandler, deleteCookie } from 'h3';

export default defineEventHandler(async (event) => {
  deleteCookie(event, 'jwt', {
    httpOnly: true,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax'
  });

  return {
    success: true,
    message: 'Desconectado com sucesso.'
  };
});
